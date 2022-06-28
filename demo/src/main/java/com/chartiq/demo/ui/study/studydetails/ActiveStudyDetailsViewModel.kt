package com.chartiq.demo.ui.study.studydetails

import android.util.Log
import androidx.annotation.ColorRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.ui.common.colorpicker.toHexStringWithHash
import com.chartiq.demo.util.Event
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.study.Study
import com.chartiq.sdk.model.study.StudyParameter
import com.chartiq.sdk.model.study.StudyParameterModel
import com.chartiq.sdk.model.study.StudyParameterType

class ActiveStudyDetailsViewModel(
    private val chartIQ: ChartIQ,
    private val study: Study
) : ViewModel() {
    private val parametersToSave = MutableLiveData<Map<String, StudyParameterModel>>(emptyMap())
    val studyParams = MutableLiveData<List<StudyParameter>>(emptyList())
    val successUpdateEvent = MutableLiveData<Event<Unit>>()
    val errorList = MutableLiveData<Set<String>>(emptySet())

    init {
        getStudyParameters()
    }

    private fun getStudyParameters() {
        chartIQ.getStudyParameters(study, StudyParameterType.Inputs) {
            studyParams.value = (studyParams.value ?: emptyList()) + it
        }
        chartIQ.getStudyParameters(study, StudyParameterType.Outputs) {
            studyParams.value = (studyParams.value ?: emptyList()) + it
        }
        chartIQ.getStudyParameters(study, StudyParameterType.Parameters) {
            studyParams.value = (studyParams.value ?: emptyList()) + it
        }
    }

    fun cloneStudy() {
        chartIQ.addStudy(study, true)
    }

    fun deleteStudy() {
        chartIQ.removeStudy(study)
    }

    fun resetStudy() {
        val newList = (studyParams.value ?: emptyList()).map {
            when (it) {
                is StudyParameter.Text -> it.copy(value = it.defaultValue)
                is StudyParameter.Number -> it.copy(value = it.defaultValue)
                is StudyParameter.Color -> it.copy(value = it.defaultValue)
                is StudyParameter.TextColor -> it.copy(
                    value = it.defaultValue,
                    color = it.defaultColor
                )
                is StudyParameter.Checkbox -> it.copy(value = it.defaultValue)
                is StudyParameter.Select -> it.copy(value = it.defaultValue)
            }
        }
        studyParams.postValue(newList)
        newList.forEach {
            when (it) {
                is StudyParameter.Text -> onTextParamChange(it, it.defaultValue)
                is StudyParameter.Number -> onNumberParamChange(it, it.defaultValue)
                is StudyParameter.Color -> onColorParamChange(it, it.defaultValue)
                is StudyParameter.TextColor -> {
                    onColorParamChange(it, it.defaultColor)
                    onNumberParamChange(it, it.defaultValue)
                }
                is StudyParameter.Checkbox -> onCheckboxParamChange(it, it.defaultValue)
                is StudyParameter.Select -> onSelectChange(it, it.defaultValue)
            }
        }
    }

    fun onCheckboxParamChange(parameter: StudyParameter.Checkbox, checked: Boolean) {
        val name = getParameterName(parameter, StudyParameter.StudyParameterNamePostfix.Enabled)
        val map = parametersToSave.value!!.toMutableMap()
        map[name] = StudyParameterModel(name, checked.toString())
        parametersToSave.value = map
    }

    fun onTextParamChange(parameter: StudyParameter, newValue: String) {
        val name = getParameterName(parameter, StudyParameter.StudyParameterNamePostfix.Value)
        val errors = errorList.value!!.toMutableSet()
        val map = parametersToSave.value!!.toMutableMap()
        if (newValue.isEmpty()) {
            errors.add(name)
            map.remove(name)
        } else {
            errors.remove(name)
            map[name] = StudyParameterModel(name, newValue)
        }
        errorList.value = errors
        parametersToSave.value = map
    }

    fun onNumberParamChange(parameter: StudyParameter, newValue: Double?) {
        val map = parametersToSave.value!!.toMutableMap()
        val errors = errorList.value!!.toMutableSet()
        val name = getParameterName(parameter, StudyParameter.StudyParameterNamePostfix.Value)
        if (newValue == null) {
            errors.add(name)
            map.remove(name)
        } else {
            errors.remove(name)
            map[name] = StudyParameterModel(name, newValue.toString())
        }
        errorList.value = errors
        parametersToSave.value = map

    }

    private fun onColorParamChange(сhangedParameter: StudyParameter, newValue: String) {
        val name =
            getParameterName(сhangedParameter, StudyParameter.StudyParameterNamePostfix.Color)
        val map = parametersToSave.value!!.toMutableMap()
        map[name] = StudyParameterModel(name, newValue)
        val updatedParams: List<StudyParameter> = (studyParams.value ?: emptyList()).map { param ->
            if (param.name == сhangedParameter.name) {
                when (param) {
                    is StudyParameter.Color -> param.copy(value = newValue)
                    is StudyParameter.TextColor -> param.copy(color = newValue)
                    else -> param
                }
            } else {
                updateParameterWithSavedData(param, map)
            }
        }
        studyParams.value = updatedParams
        parametersToSave.value = map
    }

    private fun updateParameterWithSavedData(
        param: StudyParameter,
        map: Map<String, StudyParameterModel>
    ) = when (param) {
        is StudyParameter.Color -> {
            val generatedName =
                getParameterName(param, StudyParameter.StudyParameterNamePostfix.Color)
            if (map[generatedName] != null) {
                param.copy(value = (map[generatedName] ?: error("")).fieldSelectedValue)
            } else {
                param
            }
        }
        is StudyParameter.TextColor -> {
            val generatedNameForColor =
                getParameterName(param, StudyParameter.StudyParameterNamePostfix.Color)
            val generatedNameForNumber =
                getParameterName(param, StudyParameter.StudyParameterNamePostfix.Value)
            if (listOf(
                    map[generatedNameForNumber],
                    map[generatedNameForColor]
                ).all { it == null }
            ) {
                param
            } else {
                var newParameter = param
                if (map[generatedNameForColor] != null) {
                    newParameter = param.copy(
                        color = (map[generatedNameForColor] ?: error("")).fieldSelectedValue
                    )
                }
                if (map[generatedNameForNumber] != null) {
                    newParameter =
                        param.copy(
                            value = (map[generatedNameForNumber]
                                ?: error("")).fieldSelectedValue.toDouble()
                        )
                }
                newParameter
            }
        }
        is StudyParameter.Text -> {
            val generatedName =
                getParameterName(param, StudyParameter.StudyParameterNamePostfix.Value)
            if (map[generatedName] != null) {
                param.copy(value = (map[generatedName] ?: error("")).fieldSelectedValue)
            } else {
                param
            }
        }
        is StudyParameter.Number -> {
            val generatedName =
                getParameterName(param, StudyParameter.StudyParameterNamePostfix.Value)
            if (map[generatedName] != null) {
                param.copy(value = (map[generatedName] ?: error("")).fieldSelectedValue.toDouble())
            } else {
                param
            }
        }
        is StudyParameter.Checkbox -> {
            val generatedName =
                getParameterName(param, StudyParameter.StudyParameterNamePostfix.Enabled)
            if (map[generatedName] != null) {
                param.copy(value = (map[generatedName] ?: error("")).fieldSelectedValue.toBoolean())
            } else {
                param
            }
        }
        is StudyParameter.Select -> {
            if (map[param.name] != null) {
                param.copy(value = (map[param.name] ?: error("")).fieldSelectedValue)
            } else {
                param
            }
        }
    }

    fun onSelectChange(parameter: StudyParameter.Select, newValue: String) {
        val map = parametersToSave.value!!.toMutableMap()
        map[parameter.name] = StudyParameterModel(parameter.name, newValue)
        parametersToSave.value = map
        val updatedList = updateList(studyParams.value ?: emptyList(), parameter, newValue)
        studyParams.value = updatedList
    }

    fun updateStudy() {
        parametersToSave.value?.forEach { Log.i("&&&&", it.toString()) }
        chartIQ.setStudyParameters(study, parametersToSave.value!!.values.toList())
        successUpdateEvent.postValue(Event(Unit))
    }

    private fun updateList(
        originalList: List<StudyParameter>,
        changedParameter: StudyParameter.Select,
        newValue: String
    ): List<StudyParameter> {
        return originalList.toMutableList().map {
            if (it.name == changedParameter.name) {
                changedParameter.copy(value = newValue)
            } else {
                updateParameterWithSavedData(it, parametersToSave.value ?: emptyMap())
            }
        }
    }

    private fun getParameterName(
        parameter: StudyParameter,
        postfix: StudyParameter.StudyParameterNamePostfix
    ): String {
        return if (parameter.parameterType == StudyParameterType.Parameters) {
            "${parameter.name}${postfix.name}"
        } else {
            parameter.name
        }
    }

    fun onSelectColor(parameterName: String, @ColorRes color: Int) {
        val parameter = (studyParams.value ?: emptyList()).first { it.name == parameterName }
        onColorParamChange(parameter, color.toHexStringWithHash())

    }

    class ViewModelFactory(
        private val argChartIQ: ChartIQ,
        private val argStudy: Study
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java, Study::class.java)
                .newInstance(argChartIQ, argStudy)
        }
    }
}
