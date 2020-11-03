package com.chartiq.demo.ui.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.Study
import com.chartiq.sdk.model.splitName

class StudyViewModel(
    private val chartIQHandler: ChartIQHandler
) : ViewModel() {

    fun deleteStudy(studyToDelete: Study) {
        chartIQHandler.removeStudy(studyToDelete.name)
    }

    fun cloneActiveStudy(study: Study) {
        val finalName = study.splitName()
        chartIQHandler.addStudy(finalName.first)
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