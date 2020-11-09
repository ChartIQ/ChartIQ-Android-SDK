package com.chartiq.demo.ui.study.studydetails

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.Study
import com.chartiq.sdk.model.StudyParameterType

class ActiveStudyDetailsViewModel(
    private val chartIQHandler: ChartIQHandler,
    private val study: Study
) : ViewModel() {
    val params = MutableLiveData<List<Any>>()

    init {
        getStudyParameters()
    }

    private fun getStudyParameters() {
        chartIQHandler.getStudyParameters(study, StudyParameterType.Inputs) {
            Log.i("!!! input ", it)
        }
        chartIQHandler.getStudyParameters(study, StudyParameterType.Outputs) {
            Log.i("!!! output ", it)
        }
        chartIQHandler.getStudyParameters(study, StudyParameterType.Parameters) {
            Log.i("!!! params ", it)
        }
    }

    fun cloneStudy() {
        chartIQHandler.addStudy(study, true)
    }

    fun deleteStudy() {
        chartIQHandler.removeStudy(study)
    }

    fun resetStudy() {
        // todo
    }

    class ViewModelFactory(
        private val chartIQHandler: ChartIQHandler,
        private val study: Study
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQHandler::class.java, Study::class.java)
                .newInstance(chartIQHandler, study)
        }
    }
}