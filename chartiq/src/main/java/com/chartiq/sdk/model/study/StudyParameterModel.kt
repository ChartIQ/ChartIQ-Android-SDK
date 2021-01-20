package com.chartiq.sdk.model.study

/**
 * A model to change a parameter in active study [Study]
 */
data class StudyParameterModel(
    val fieldName: String,
    val fieldSelectedValue: String
)
