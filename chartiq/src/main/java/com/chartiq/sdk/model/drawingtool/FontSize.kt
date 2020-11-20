package com.chartiq.sdk.model.drawingtool

import com.google.gson.annotations.SerializedName

enum class FontSize(val pxValue: String) {
    @SerializedName("8px")
    SIZE_8("8px"),

    @SerializedName("10px")
    SIZE_10("10px"),

    @SerializedName("12px")
    SIZE_12("12px"),

    @SerializedName("13px")
    SIZE_13("13px"),

    @SerializedName("14px")
    SIZE_14("14px"),

    @SerializedName("16px")
    SIZE_16("16px"),

    @SerializedName("20px")
    SIZE_20("20px"),

    @SerializedName("28px")
    SIZE_28("28px"),

    @SerializedName("36px")
    SIZE_36("36px"),

    @SerializedName("48px")
    SIZE_48("48px"),

    @SerializedName("64px")
    SIZE_64("64px")
}
