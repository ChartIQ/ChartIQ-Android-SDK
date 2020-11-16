package com.chartiq.demo.ui.study.studydetails

import android.util.Log
import androidx.lifecycle.*
import com.chartiq.demo.util.Event
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
        Log.i("&&&", "!!!")
        (output ?: emptyList()) + (input ?: emptyList()) + (param ?: emptyList())
    }
    private val parametersToSave = MutableLiveData<Map<String, StudyParameterKeyValue>>(emptyMap())
    val successUpdateEvent = MutableLiveData<Event<Unit>>()

    init {
        getStudyParameters()
    }

    private fun getStudyParameters() {
        chartIQHandler.getStudyParameters(study, StudyParameterType.Inputs) {
            inputParameters.postValue(it)
        }
        chartIQHandler.getStudyParameters(study, StudyParameterType.Outputs) {
            outputParameters.postValue(it)
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

    fun onSelectChange(parameter: StudyParameter.Select, newValue: String) {
        if (parameter.value != newValue) {
            val map = parametersToSave.value!!.toMutableMap()
            map[parameter.name] = StudyParameterKeyValue(parameter.name, newValue)
            parametersToSave.postValue(map)
        }
        val changedParameter = studyParams.value!!
            .filterIsInstance<StudyParameter.Select>()
            .find { it.name == parameter.name }!!

        when (changedParameter.parameterType) {
            StudyParameterType.Inputs -> inputParameters.value =
                updateList(inputParameters.value ?: emptyList(), parameter, newValue)
            StudyParameterType.Outputs -> outputParameters.value =
                updateList(outputParameters.value ?: emptyList(), parameter, newValue)
            StudyParameterType.Parameters -> parameters.value =
                updateList(parameters.value ?: emptyList(), parameter, newValue)
        }
    }

    private fun updateList(
        originalList: List<StudyParameter>,
        changedParameter: StudyParameter.Select,
        newValue: String
    ): List<StudyParameter> {
        return originalList.toMutableList().map {
            if (it.name == changedParameter.name) {
                changedParameter.copy(value = newValue)
            } else it
        }
    }

    fun updateStudy() {
        chartIQHandler.setStudyParameters(study, parametersToSave.value!!.values.toList())
        successUpdateEvent.postValue(Event(Unit))
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
