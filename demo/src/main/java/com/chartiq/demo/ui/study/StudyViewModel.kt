package com.chartiq.demo.ui.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.Study

class StudyViewModel(
    private val chartIQ: ChartIQ
) : ViewModel() {

    fun deleteStudy(studyToDelete: Study) {
        chartIQ.removeStudy(studyToDelete)
    }

    /**
     * In case we want to add a study selected from [ChartIQ.getActiveStudies] list
     * we should send [Study.type] to [ChartIQ.addStudy]
     */
    fun cloneActiveStudy(study: Study) {
        study.type?.let { type ->
            chartIQ.addStudy(study, true)
        }
    }

    class ViewModelFactory(
        private val argChartIQ: ChartIQ
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java)
                .newInstance(argChartIQ)
        }
    }
}
