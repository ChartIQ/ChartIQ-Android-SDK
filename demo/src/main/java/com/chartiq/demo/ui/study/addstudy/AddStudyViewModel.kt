package com.chartiq.demo.ui.study.addstudy

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.ui.localization.LocalizationManager
import com.chartiq.demo.util.combineLatest
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.study.Study
import java.util.*

class AddStudyViewModel(
    private val chartIQ: ChartIQ,
    private val context: Context
) : ViewModel() {

    private val originalStudies = MutableLiveData<List<Study>>(emptyList())

    private val selectedStudies = MutableLiveData<List<Study>>(emptyList())

    private val query = MutableLiveData("")

    val filteredStudies =
        Transformations.map(originalStudies.combineLatest(query)) { (list, query) ->
            val filtered = list
                .filter {
                    LocalizationManager.getTranslationFromValue(it.name, context)
                        .toLowerCase(Locale.getDefault())
                        .contains(query.toLowerCase(Locale.getDefault()))
                }
            filtered
        }

    init {
        chartIQ.getStudyList { list ->
            originalStudies.postValue(list.sortedBy { it.name })
        }
    }

    fun saveStudies() {
        val finalList = selectedStudies.value ?: emptyList()
        finalList.forEach {
            chartIQ.addStudy(it, false)
        }
    }

    fun onStudiesSelect(studies: List<Study>) {
        selectedStudies.postValue(studies)
    }

    fun onNewQuery(value: String) {
        query.postValue(value)
    }

    class ViewModelFactory(
        private val chartIQ: ChartIQ,
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java, Context::class.java)
                .newInstance(chartIQ, context)
        }
    }
}
