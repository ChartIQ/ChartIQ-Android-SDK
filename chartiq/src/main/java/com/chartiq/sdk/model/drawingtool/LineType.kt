package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

enum class LineType(val value: String) {
    @SerializedName("solid")
    SOLID("solid"),

    @SerializedName("dotted")
    DOTTED("dotted"),

    @SerializedName("dashed")
    DASHED("dashed")
}
