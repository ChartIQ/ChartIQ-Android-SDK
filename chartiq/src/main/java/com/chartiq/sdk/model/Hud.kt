package com.chartiq.sdk.model

import java.io.Serializable

data class Hud(
    val close: String,
    val open: String,
    val high: String,
    val low: String,
    val volume: String
) : Serializable
