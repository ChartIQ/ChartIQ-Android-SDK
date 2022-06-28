package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class SignalShape(val title: String) : Parcelable {
    CIRCLE("Circle"),
    SQUARE("Square"),
    DIAMOND("Diamond")
}