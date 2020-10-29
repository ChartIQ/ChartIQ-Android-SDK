package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

data class Fib(
    @SerializedName("level")
    val level: Double,
    @SerializedName("display")
    val display: Boolean
)
