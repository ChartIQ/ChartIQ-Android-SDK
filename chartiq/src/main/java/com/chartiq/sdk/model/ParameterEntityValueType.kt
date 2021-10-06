package com.chartiq.sdk.model


internal enum class ParameterEntityValueType(
    /**
     * @suppress
     */
    val value: String
) {
    NUMBER("number"),
    SELECT("select"),
    CHECKBOX("checkbox"),
    TEXT("text")
}
