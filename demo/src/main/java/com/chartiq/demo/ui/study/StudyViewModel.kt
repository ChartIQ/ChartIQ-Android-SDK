package com.chartiq.demo.ui.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQ

import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.study.Study

class StudyViewModel(private val chartIQ: ChartIQ) : ViewModel() {

    fun deleteStudy(studyToDelete: Study) {
        chartIQ.removeStudy(studyToDelete)
    }

    /**
     * In case we want to add a study selected from [ChartIQHandler.getActiveStudies] list
     * `forClone = true`
     */
    fun cloneActiveStudy(study: Study) {
        chartIQ.addStudy(study, true)
    }

    class ViewModelFactory(private val chartIQ: ChartIQ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java)
                .newInstance(chartIQ)
        }
    }
}
