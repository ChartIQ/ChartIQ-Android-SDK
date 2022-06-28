package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class SignalPosition(val title: String) : Parcelable {
    ABOVE_CANDLE("Above candle"),
    BELOW_CANDLE("Below candle"),
    ON_CANDLE("On candle")
}