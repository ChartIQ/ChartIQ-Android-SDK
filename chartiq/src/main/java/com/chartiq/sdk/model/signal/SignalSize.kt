package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class SignalSize(val key: String) : Parcelable {
    S("small"),
    M("medium"),
    L("large");

    companion object {
        fun from(title: String): SignalSize {
            return when (title) {
                "S" -> S
                "M" -> M
                "L" -> L
                else -> M
            }
        }
    }
}