package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class SignalSize(val title: String) : Parcelable {
    S("Small"),
    M("Medium"),
    L("Large")
}