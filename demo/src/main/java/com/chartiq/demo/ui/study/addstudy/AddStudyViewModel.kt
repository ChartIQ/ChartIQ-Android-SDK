package com.chartiq.demo.ui.study.addstudy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.util.combineLatest
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.Study
import java.util.*

class AddStudyViewModel(private val chartIQHandler: ChartIQ) : ViewModel() {

    private val originalStudies = MutableLiveData<List<Study>>(emptyList())

    private val selectedStudies = MutableLiveData<List<Study>>(emptyList())

    private val query = MutableLiveData("")

    val filteredStudies =
        Transformations.map(originalStudies.combineLatest(query)) { (list, query) ->
            val filtered = list.filter {
                it.name
                    .toLowerCase(Locale.getDefault())
                    .contains(query.toLowerCase(Locale.getDefault()))
            }
            filtered
        }

    init {
        chartIQHandler.getStudyList { list ->
            originalStudies.value = list.sortedBy { it.name }
        }
    }

    /**
     * In case we want to add a study selected from [ChartIQHandler.getStudyList] list
     * we should send [Study.name] to [ChartIQHandler.addStudy]
     */
    fun saveStudies() {
        val finalList = selectedStudies.value ?: emptyList()
        finalList.forEach {
            chartIQHandler.addStudy(it.name)
        }
    }

    fun onStudiesSelect(studies: List<Study>) {
        selectedStudies.postValue(studies)
    }

    fun onNewQuery(value: String) {
        query.postValue(value)
    }

    class ViewModelFactory(private val chartIQHandler: ChartIQ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java)
                .newInstance(chartIQHandler)
        }
    }
}
