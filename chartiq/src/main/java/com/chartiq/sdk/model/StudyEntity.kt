package com.chartiq.sdk.model

import java.io.Serializable

 data class StudyEntity(
    val attributes: Map<String, Object>?,
    val centerLine: Double,
    val customRemoval: Boolean,
    val deferUpdate: Boolean,
    val display: String?,
    var inputs: Map<String, Object>?,
    var my_outputs: List<Map<String, Object>>?,
    val name: String,
    val overlay: Boolean,
    var parameters: Map<String, Object>?,
    val range: String?,
    val shortName: String?,
    val type: String?,
    val underlay: Boolean,
    val yAxis: Map<String, Object>?
) : Serializable{
     
 }

fun StudyEntity.toStudy(): Study {
    return Study(
        attributes = attributes ?: emptyMap(),
        centerLine = centerLine,
        customRemoval = customRemoval,
        deferUpdate = deferUpdate,
        display = display ?: "",
        inputs = inputs ?: emptyMap(),
        outputs = my_outputs?.firstOrNull() ?: emptyMap(),
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