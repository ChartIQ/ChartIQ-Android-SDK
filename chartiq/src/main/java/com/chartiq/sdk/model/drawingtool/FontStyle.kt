package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

// TODO: 18.12.20 check in settings if enum can be used
enum class FontStyle(val value: String) {
    @SerializedName("bold")
    BOLD("bold"),

    @SerializedName("italic")
    ITALIC("italic"),

    @SerializedName("normal")
    NORMAL("normal")
}
