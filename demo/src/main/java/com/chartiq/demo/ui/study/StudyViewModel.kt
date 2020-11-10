package com.chartiq.demo.ui.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.Study

class StudyViewModel(private val chartIQHandler: ChartIQ) : ViewModel() {

    fun deleteStudy(studyToDelete: Study) {
        chartIQHandler.removeStudy(studyToDelete)
    }

    class ViewModelFactory(private val chartIQHandler: ChartIQ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java)
                .newInstance(chartIQHandler)
        }
    }
}
