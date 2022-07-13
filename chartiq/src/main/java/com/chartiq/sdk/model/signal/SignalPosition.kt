package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class SignalPosition(val key: String) : Parcelable {
    ABOVE_CANDLE("above_candle"),
    BELOW_CANDLE("below_candle"),
    ON_CANDLE("on_candle");

    companion object {
        fun from(title: String): SignalPosition {
            return when (title) {
                "above_candle" -> ABOVE_CANDLE
                "below_candle" -> BELOW_CANDLE
                "on_candle" -> ON_CANDLE
                else -> ABOVE_CANDLE
            }
        }
    }
}