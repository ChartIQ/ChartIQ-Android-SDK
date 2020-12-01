package com.chartiq.sdk.model

import com.google.gson.annotations.SerializedName

data class CrosshairHUD(
    @SerializedName("price")
    val price: String,
    @SerializedName("volume")
    val volume: String,
    @SerializedName("open")
    val open: String,
    @SerializedName("high")
    val high: String,
    @SerializedName("close")
    val close: String,
    @SerializedName("low")
    val low: String
)
