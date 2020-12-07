package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

enum class FontStyle(val value: String) {
    @SerializedName("bold")
    BOLD("bold"),

    @SerializedName("italic")
    ITALIC("italic"),

    @SerializedName("normal")
    NORMAL("normal")
}
