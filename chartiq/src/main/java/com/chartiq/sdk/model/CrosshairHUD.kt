package com.chartiq.sdk.model

import com.google.gson.annotations.SerializedName

/**
 * A heads-up display (HUD) is a method of displaying detailed information for a specific bar on the chart itself.
 * The information for the bar that the cursor is currently hovering over is presented in the HUD.
 * @property price Price for the bar.
 * @property volume Trading volume for the bar in whole numbers
 * @property open Opening price for the bar.
 * @property high High price for the bar.
 * @property close Closing price for the bar.
 * @property low Low price for the bar.
 */
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
