package com.chartiq.sdk.model.study

import java.io.Serializable

internal data class StudyEntity(
    val attributes: Map<String, Object>?,
    val centerLine: Double,
    val customRemoval: Boolean,
    val deferUpdate: Boolean,
    val display: String?,
    var parsed_inputs: List<Map<String, Object>>?,
    var parsed_outputs: List<Map<String, Object>>?,
    val name: String,
    val overlay: Boolean,
    var parameters: Map<String, Object>?,
    val range: String?,
    val shortName: String?,
    val type: String?,
    val underlay: Boolean,
    val yAxis: Map<String, Object>?
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
        yAxis = yAxis ?: emptyMap()
    )
}
