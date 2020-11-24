package com.chartiq.sdk.model

import com.google.gson.annotations.SerializedName

data class StudyParameterEntity(
    @SerializedName("defaultInput", alternate = ["defaultValue", "defaultOutput"])
    val defaultValue: Any,
    val heading: String,
    val name: String,
    val type: String,
    val value: Any,
    val options: Map<String, String>?,
    val color: String?,
    val defaultColor: String?,
)

fun StudyParameterEntity.toParameter(parameterType: StudyParameterType): StudyParameter {
    return when (type) {
        ParameterEntityValueType.NUMBER.value -> {
            StudyParameter.Number(
                value = (value as Double),
                defaultValue = (this.defaultValue as Double),
                heading = heading,
                name = name,
                parameterType = parameterType,
            )
        }
        ParameterEntityValueType.SELECT.value -> {
            StudyParameter.Select(
                value = (value as String),
                defaultValue = (this.defaultValue as String),
                heading = heading,
                name = name,
                parameterType = parameterType,
                options = options ?: emptyMap()
            )
        }
        ParameterEntityValueType.CHECKBOX.value -> {
            StudyParameter.Checkbox(
                value = (value as Boolean),
                defaultValue = (this.defaultValue as Boolean),
                heading = heading,
                name = name,
                parameterType = parameterType,
            )
        }
        ParameterEntityValueType.TEXT.value -> {
            if (defaultColor != null) {
                StudyParameter.TextColor(
                    value = (value as Double),
                    defaultValue = (defaultValue as Double),
                    heading = heading,
                    name = name,
                    defaultColor = defaultColor,
                    color = color ?: defaultColor,
                    parameterType = parameterType,
                )
            } else {
                StudyParameter.Text(
                    value = (value as String),
                    heading = heading,
                    name = name,
                    parameterType = parameterType,
                    defaultValue = defaultValue as String
                )
            }
        }
        else ->
            if(color != null){
                StudyParameter.Color(
                    value = color,
                    heading = heading,
                    name = name,
                    defaultValue = defaultValue as String,
                    parameterType = parameterType
                )
            }
            else if (defaultValue == StudyParameter.AUTO_VALUE) {
                StudyParameter.Color(
                    value = color ?: defaultValue as String,
                    heading = heading,
                    name = name,
                    defaultValue = defaultValue as String,
                    parameterType = parameterType
                )
            } else {
                throw IllegalArgumentException("Invalid Study Parameter $this")
            }
    }
}
