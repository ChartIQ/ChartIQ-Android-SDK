package com.chartiq.sdk.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * A data class of OHLC parameters
 * @property date A date string, representing the start time of the bar or tick, in case a DT compatible value is not available.
 * @property open Opening price for the bar. Required for candle charts only.
 * @property high High price for the bar. Required for candle charts only.
 * @property low Low price for the bar. Required for candle charts only.
 * @property close Closing price for the bar. Excluding or setting this field to null will cause the chart to display a gap for this bar.
 * @property volume Trading volume for the bar in whole numbers.
 * @property adjClose Closing price adjusted price after splits or dividends. This is only necessary if you wish to give users the ability to display both adjusted and unadjusted values.
 */
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
