package com.chartiq.sdk.model

data class Study(
    val attributes: Map<String, Any>?,
    val centerLine: Double,
    val customRemoval: Boolean,
    val deferUpdate: Boolean,
    val display: String?,
    var inputs: Map<String, Any>?,
    var outputs: Map<String, Any>?,
    val name: String,
    val overlay: Boolean,
    var parameters: Map<String, Any>,
    val range: String?,
    val shortName: String,
    val type: String?,
    val underlay: Boolean,
    val yAxis: Map<String, Any>?,
)