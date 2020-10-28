package com.chartiq.demo.ui.study

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.Study

class StudyViewModel(
    private val chartIQHandler: ChartIQHandler,
) : ViewModel() {

    val activeStudies = MutableLiveData<List<Study>>(emptyList())

    init {
        fetchActiveStudies()
    }

    private fun fetchActiveStudies() {
        chartIQHandler.getStudyList() { result -> activeStudies.postValue(result) }
    }

    fun deleteStudy(studyToDelete: Study) {
        chartIQHandler.removeStudy(studyToDelete.shortName)
        fetchActiveStudies()
    }
    class ViewModelFactory(
        private val  chartIQHandler: ChartIQHandler
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQHandler::class.java)
                .newInstance(chartIQHandler)
        }
    }
}