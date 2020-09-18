package com.chartiq.sdk.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class OHLCParams(
    @SerializedName("DT")
    val date: Date?,
    @SerializedName("Open")
    val open: Double?,
    @SerializedName("High")
    val high: Double?,
    @SerializedName("Low")
    val low: Double?,
    @SerializedName("Close")
    val close: Double?,
    @SerializedName("Volume")
    val volume: Double?,
    @SerializedName("AdjClose")
    val adjClose: Double?
)
