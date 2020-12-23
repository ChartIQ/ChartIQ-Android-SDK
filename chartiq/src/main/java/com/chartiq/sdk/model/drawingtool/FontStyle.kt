package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

// TODO: 19.12.20 Discuss if it can be @SerializedName
enum class FontStyle(val value: String) {
    @SerializedName("bold")
    BOLD("bold"),

    @SerializedName("italic")
    ITALIC("italic"),

    @SerializedName("normal")
    NORMAL("normal")
}
