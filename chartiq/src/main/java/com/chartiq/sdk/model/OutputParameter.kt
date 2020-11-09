package com.chartiq.sdk.model

sealed class InputParameter {
    abstract val heading: String
    abstract val name: String

    data class Text(
        override val heading: String,
        override val name: String,
        val defaultOutput: String,
        val value: String
    ) : InputParameter()

    data class Number(
        override val heading: String,
        override val name: String,
        val defaultOutput: Double,
        val value: Double
    ) : InputParameter()

    data class Color(
        override val heading: String,
        override val name: String,
        val defaultOutput: Double,
        val value: Double
    ) : InputParameter()

    data class TextColor(
        override val heading: String,
        override val name: String,
        val defaultOutput: Double,
        val value: Double
    ) : InputParameter()
}

data class OutputInputParameter(
    val defaultInput: String,
    val color: String?,
    val name: String,
)

enum class ParameterType {
    Text, Number, Color, TextColor, Toggle, Select
}