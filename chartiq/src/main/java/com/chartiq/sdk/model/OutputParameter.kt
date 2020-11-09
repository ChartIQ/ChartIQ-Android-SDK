package com.chartiq.sdk.model

data class OutputStudyParameter(
    val defaultOutput: String,
    val color: String?,
    val name: String,
    val heading: String
)

sealed class StudyParameter {
    abstract val heading: String
    abstract val name: String
    abstract val parameterType: StudyParameterType

    data class Text(
        override val heading: String,
        override val name: String,
        override val parameterType: StudyParameterType,
        val value: String
    ) : StudyParameter()

    data class Number(
        override val heading: String,
        override val name: String,
        override val parameterType: StudyParameterType,
        val defaultValue: Double,
        val value: Double
    ) : StudyParameter()

    data class Color(
        override val heading: String,
        override val name: String,
        override val parameterType: StudyParameterType,
        val defaultValue: String,
        val value: String
    ) : StudyParameter()

    data class TextColor(
        override val heading: String,
        override val name: String,
        override val parameterType: StudyParameterType,
        val defaultValue: Double,
        val value: Double
    ) : StudyParameter()

    data class Checkbox(
        override val heading: String,
        override val name: String,
        override val parameterType: StudyParameterType,
        val defaultValue: Boolean,
        val value: Boolean
    ) : StudyParameter()

    data class Select(
        override val heading: String,
        override val name: String,
        override val parameterType: StudyParameterType,
        val defaultValue: String,
        val value: String,
        val options: Map<String, String>
    ) : StudyParameter()
}




