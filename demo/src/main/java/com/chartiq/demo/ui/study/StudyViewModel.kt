package com.chartiq.demo.ui.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.Study

class StudyViewModel(
    private val chartIQHandler: ChartIQHandler
) : ViewModel() {

    fun deleteStudy(studyToDelete: Study) {
        chartIQHandler.removeStudy(studyToDelete)
    }

    /**
     * In case we want to add a study selected from [ChartIQHandler.getActiveStudies] list
     * we should send [Study.type] to [ChartIQHandler.addStudy]
     */
    fun cloneActiveStudy(study: Study) {
        study.type?.let { type ->
            chartIQHandler.addStudy(type)
        }
    }

    class ViewModelFactory(
        private val chartIQHandler: ChartIQHandler
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQHandler::class.java)
                .newInstance(chartIQHandler)
        }
    }
}