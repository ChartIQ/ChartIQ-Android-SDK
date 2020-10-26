package com.chartiq.sdk.model

import java.io.Serializable

// TODO: 02.09.20 Have a look if parameters can actually be var
// todo replace Objects with more concrete types
data class Study(
    val attributes: Map<String, Object>,
    val centerLine: Double,
    val customRemoval: Boolean,
    val deferUpdate: Boolean,
    val display: String,
    var inputs: Map<String, Object>?,
    var outputs: Map<String, Object>?,
    val name: String,
    val overlay: Boolean,
    var parameters: Map<String, Object>,
    val range: String,
    val shortName: String,
    val underlay: Boolean,
    val yAxis: Map<String, Object>,
    val type: String?
) : Serializable
