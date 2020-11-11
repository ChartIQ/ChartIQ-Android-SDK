package com.chartiq.demo.ui.study.studydetails

import androidx.lifecycle.*
import com.chartiq.demo.util.combineLatest
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.Study
import com.chartiq.sdk.model.StudyParameter
import com.chartiq.sdk.model.StudyParameterKeyValue
import com.chartiq.sdk.model.StudyParameterType

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
    private val parametersToSave = MutableLiveData<Map<String, StudyParameterKeyValue>>(emptyMap())

    val canUpdateParameters = Transformations.map(parametersToSave) { !it.isNullOrEmpty() }

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

    fun onCheckboxParamChange(parameter: StudyParameter.Checkbox, checked: Boolean) {
        if (parameter.value != checked) {
            val map = parametersToSave.value!!.toMutableMap()
            map[parameter.name] = StudyParameterKeyValue(parameter.name, checked.toString())
            parametersToSave.postValue(map)
        }
    }

    fun onTextParamChange(parameter: StudyParameter, newValue: String) {
        if (parameter is StudyParameter.TextColor && parameter.value.toString() != newValue
            || parameter is StudyParameter.Text && parameter.value != newValue
        ) {
            val map = parametersToSave.value!!.toMutableMap()
            map[parameter.name] = StudyParameterKeyValue(parameter.name, newValue)
            parametersToSave.postValue(map)
        }
    }

    fun onNumberParamChange(parameter: StudyParameter.Number, newValue: Double) {
        if (parameter.value != newValue) {
            val map = parametersToSave.value!!.toMutableMap()
            map[parameter.name] = StudyParameterKeyValue(parameter.name, newValue.toString())
            parametersToSave.postValue(map)
        }
    }

    fun updateStudy() {
        //todo check with [ChartIQHandler.setStudyParameters]
        parametersToSave.value!!.forEach {
            chartIQHandler.setStudyParameter(study, it.value)
        }
//        chartIQHandler.setStudyParameters(study, parametersToSave.value!!.values.toList())
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
