package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class SignalMarkerType(val key: String) : Parcelable {
    MARKER("marker"),
    PAINTBAR("paintbar");

    companion object {
        fun from(title: String): SignalMarkerType {
            return when (title) {
                "marker" -> MARKER
                "paintbar" -> PAINTBAR
                else -> PAINTBAR
            }
        }
    }
}

