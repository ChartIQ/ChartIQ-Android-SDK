package com.chartiq.demo.ui.addstudy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.util.combineLatest
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.Study
import java.util.*

class AddStudyViewModel(
    private val chartIQHandler: ChartIQHandler,
) : ViewModel() {
    private val originalStudies = MutableLiveData<List<Study>>(emptyList())

    val selectedStudies = MutableLiveData<List<Study>>(emptyList())

    val query = MutableLiveData("")

    val filteredStudies =
        Transformations.map(originalStudies.combineLatest(query)) { (list, query) ->
            val filtered = list.filter {
                it.name.toLowerCase(Locale.getDefault())
                    .contains(query.toLowerCase(Locale.getDefault()))
            }
            filtered
        }

    init {
        chartIQHandler.getStudyList {
            originalStudies.postValue(it.sortedBy { it.name })
        }
    }

    fun saveStudies() {
        val finalList = selectedStudies.value ?: emptyList()
        finalList.forEach {
            chartIQHandler.addStudy(it, false)
        }
    }

    class ViewModelFactory(private val chartIQHandler: ChartIQHandler) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQHandler::class.java)
                .newInstance(chartIQHandler)
        }
    }
}
