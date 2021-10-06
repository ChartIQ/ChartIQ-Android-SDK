package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

/**
 * An enumeration of available font styles
 */
enum class FontStyle(
    /**
     * @suppress
     */
    val value: String
) {
    @SerializedName("bold")
    BOLD("bold"),

    @SerializedName("italic")
    ITALIC("italic"),

    @SerializedName("normal")
    NORMAL("normal")
}
