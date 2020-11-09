package com.chartiq.demo.ui.study.studydetails

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.*

class ActiveStudyDetailsViewModel(
    private val chartIQHandler: ChartIQHandler,
    private val study: Study
) : ViewModel() {
    val studyParams = MutableLiveData<List<StudyParameter>>()

    init {
        getStudyParameters()
    }

    private fun getStudyParameters() {
        chartIQHandler.getStudyParameters(study, StudyParameterType.Inputs) {
            Log.i("!!!", it.toString())
            studyParams.postValue(it)
        }
//        chartIQHandler.getStudyParameters(study, StudyParameterType.Outputs) {
////            Log.i("!!! output ", it)
//        }
//        chartIQHandler.getStudyParameters(study, StudyParameterType.Parameters) {
////            Log.i("!!! params ", it)
//        }
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