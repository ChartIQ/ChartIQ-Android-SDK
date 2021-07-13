package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

/**
 * An enumeration of available font families
 */
enum class FontFamily(
    /**
     * @suppress
     */
    val value: String
) {
    @SerializedName("Default")
    DEFAULT("Default"),

    @SerializedName("Helvetica")
    HELVETICA("Helvetica"),

    @SerializedName("Courier")
    COURIER("Courier"),

    @SerializedName("Garamond")
    GARAMOND("Garamond"),

    @SerializedName("Palatino")
    PALATINO("Palatino"),

    @SerializedName("Times New Roman")
    TIMES_NEW_ROMAN("Times New Roman")
}
