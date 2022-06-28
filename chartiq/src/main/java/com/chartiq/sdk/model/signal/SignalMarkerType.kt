package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class SignalMarkerType(val title: String) : Parcelable {
    MARKER("Chart marker"),
    PAINTBAR("Paintbar")
}
