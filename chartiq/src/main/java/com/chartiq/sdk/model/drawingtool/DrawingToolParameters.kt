package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

data class DrawingToolParameters(
    @SerializedName("pattern")
    val pattern: LineType,
    @SerializedName("lineWidth")
    val lineWidth: Int,
    @SerializedName("fillColor")
    val fillColor: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("font")
    val font: Font,
    @SerializedName("fibs")
    val fibs: List<Fib>
)
