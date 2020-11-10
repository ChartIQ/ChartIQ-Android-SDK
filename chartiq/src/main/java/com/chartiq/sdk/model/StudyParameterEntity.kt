package com.chartiq.sdk.model

data class StudyParameterEntity(
    val defaultInput: Any, //Double/String/Boolean
    val heading: String,
    val name: String,
    val type: String, // number(Double), select(String), checkbox(Boolean)
    val value: Any, //Double/String/Boolean
    val options: Map<String, String>?,
// outputs
    val color: String?,
    val defaultOutput: String,
//  val heading
// val name

)

fun StudyParameterEntity.toInputParameter(): StudyParameter {
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
        name = "undefined",
        parameterType = StudyParameterType.Inputs
    )
}

fun StudyParameterEntity.toOutputParameter(): StudyParameter {
    return StudyParameter.Color(
        value = color ?: defaultOutput,
        heading = heading,
        name = name,
        defaultValue = defaultOutput,
        parameterType = StudyParameterType.Inputs
    )
}
