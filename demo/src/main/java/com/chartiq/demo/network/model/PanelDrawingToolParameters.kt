package com.chartiq.demo.network.model

import com.chartiq.sdk.model.drawingtool.Fib
import com.chartiq.sdk.model.drawingtool.Font
import com.chartiq.sdk.model.drawingtool.LineType
import com.google.gson.annotations.SerializedName

data class PanelDrawingToolParameters(
    @SerializedName("pattern")
    val lineType: LineType?,
    @SerializedName("lineWidth")
    val lineWidth: Int?,
    @SerializedName("fillColor")
    val fillColor: String?,
    @SerializedName("color")
    val color: String?,
    @SerializedName("font")
    val font: Font?,
    @SerializedName("fibs")
    val fibs: List<Fib>
)
