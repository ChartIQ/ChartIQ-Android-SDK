package com.chartiq.demo.network.model

import com.chartiq.sdk.model.drawingtool.FontFamily
import com.chartiq.sdk.model.drawingtool.FontStyle
import com.google.gson.annotations.SerializedName

data class Font(
    @SerializedName("style")
    val style: FontStyle,
    @SerializedName("size")
    val size: String,
    @SerializedName("weight")
    val weight: String,
    @SerializedName("family")
    val family: FontFamily
)
