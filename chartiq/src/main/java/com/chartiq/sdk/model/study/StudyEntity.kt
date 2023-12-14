package com.chartiq.sdk.model.study

import java.io.Serializable

data class StudyEntity(
    val attributes: Map<String, Any>?,
    val centerLine: Double,
    val customRemoval: Boolean,
    val deferUpdate: Boolean,
    val display: String?,
    var parsed_inputs: List<Map<String, Any>>?,
    var parsed_outputs: List<Map<String, Any>>?,
    val name: String,
    val overlay: Boolean,
    var parameters: Map<String, Any>?,
    val range: String?,
    val shortName: String?,
    val type: String?,
    val underlay: Boolean,
    val yAxis: Map<String, Any>?,
    val signalIQExclude: Boolean
) : Serializable


internal fun StudyEntity.toStudy(): Study {
    return Study(
        attributes = attributes ?: emptyMap(),
        centerLine = centerLine,
        customRemoval = customRemoval,
        deferUpdate = deferUpdate,
        display = display ?: "",
        inputs = parsed_inputs?.firstOrNull() ?: emptyMap(),
        outputs = parsed_outputs?.firstOrNull() ?: emptyMap(),
        name = name,
        overlay = overlay,
        parameters = parameters ?: emptyMap(),
        range = range ?: "",
        shortName = shortName ?: name,
        type = type ?: "",
        underlay = underlay,
        yAxis = yAxis ?: emptyMap(),
        signalIQExclude = signalIQExclude
    )
}
