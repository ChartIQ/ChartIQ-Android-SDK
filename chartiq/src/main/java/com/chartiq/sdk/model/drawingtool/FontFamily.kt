package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

enum class FontFamily {
    @SerializedName("Default")
    DEFAULT,

    @SerializedName("Helvetica")
    HELVETICA,

    @SerializedName("Courier")
    COURIER,

    @SerializedName("Garamond")
    GARAMOND,

    @SerializedName("Palatino")
    PALATINO,

    @SerializedName("Times New Roman")
    TIMES_NEW_ROMAN
}
