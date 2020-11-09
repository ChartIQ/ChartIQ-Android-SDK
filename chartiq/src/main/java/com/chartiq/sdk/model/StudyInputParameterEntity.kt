package com.chartiq.sdk.model

data class StudyInputParameterEntity(
    val defaultInput: Any, //Double/String/Boolean
    val heading: String,
    val name: String,
    val type: String, // number(Double), select(String), checkbox(Boolean)
    val value: Any, //Double/String/Boolean
    val options: Map<String, String>?

)

fun StudyInputParameterEntity.toInputParameter(): StudyParameter {
    when (type) {
        "number" -> {
            return StudyParameter.Number(
                value = (value as Double),
                defaultValue = (this.defaultInput as Double),
                heading = heading,
                name = name,
                parameterType = StudyParameterType.Inputs,
            )
        }
        "select" -> {
            return StudyParameter.Select(
                value = (value as String),
                defaultValue = (this.defaultInput as String),
                heading = heading,
                name = name,
                parameterType = StudyParameterType.Inputs,
                options = options ?: emptyMap()
            )
        }
        "checkbox" -> {
            return StudyParameter.Checkbox(
                value = (value as Boolean),
                defaultValue = (this.defaultInput as Boolean),
                heading = heading,
                name = name,
                parameterType = StudyParameterType.Inputs,
            )
        }
    }
    return StudyParameter.Text(
        value = "undefined",
        heading = "undefined",
        name = "undefine",
        parameterType = StudyParameterType.Inputs
    )
}
