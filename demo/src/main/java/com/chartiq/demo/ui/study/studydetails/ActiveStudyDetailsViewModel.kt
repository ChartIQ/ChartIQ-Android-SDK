package com.chartiq.demo.ui.study.studydetails

import android.util.Log
import androidx.lifecycle.*
import com.chartiq.demo.util.combineLatest
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.*

class ActiveStudyDetailsViewModel(
    private val chartIQHandler: ChartIQHandler,
    private val study: Study
) : ViewModel() {
    private val outputParameters = MutableLiveData<List<StudyParameter>>()
    private val inputParameters = MutableLiveData<List<StudyParameter>>()
    private val parameters = MutableLiveData<List<StudyParameter>>()

    val studyParams: LiveData<List<StudyParameter>> = Transformations.map(
        combineLatest(outputParameters, inputParameters, parameters)
    ) { (output, input, param) ->
        (output ?: emptyList()) + (input ?: emptyList()) + (param ?: emptyList())
    }

    init {
        getStudyParameters()
    }

    private fun getStudyParameters() {
        chartIQHandler.getStudyParameters(study, StudyParameterType.Inputs) {
            outputParameters.postValue(it)
        }
        chartIQHandler.getStudyParameters(study, StudyParameterType.Outputs) {
            inputParameters.postValue(it)
        }
        chartIQHandler.getStudyParameters(study, StudyParameterType.Parameters) {
            parameters.postValue(it)
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