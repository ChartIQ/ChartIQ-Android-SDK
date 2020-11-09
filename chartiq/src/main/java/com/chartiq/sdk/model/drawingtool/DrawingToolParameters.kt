package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

data class DrawingToolParameters(
    @SerializedName("pattern")
    val lineType: LineType?,
    @SerializedName("lineWidth")
    val lineWidth: Int?,
    @SerializedName("fillColor")
    val fillColor: String?,
    @SerializedName("color")
    val color: String?,
    @SerializedName("font")
    val font: Font,
    @SerializedName("fibs")
    val fibs: List<Fib>
)
