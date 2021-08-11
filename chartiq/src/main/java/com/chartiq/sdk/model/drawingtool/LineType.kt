package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

/**
 * An enumeration of available line types
 */
enum class LineType(
    /**
     * @suppress
     */
    val value: String
) {
    @SerializedName("solid")
    SOLID("solid"),

    @SerializedName("dotted")
    DOTTED("dotted"),

    @SerializedName("dashed")
    DASHED("dashed")
}
