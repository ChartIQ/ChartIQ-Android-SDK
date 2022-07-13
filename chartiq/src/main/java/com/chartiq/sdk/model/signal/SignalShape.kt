package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class SignalShape(val key: String) : Parcelable {
    CIRCLE("circle"),
    SQUARE("square"),
    DIAMOND("diamond");

    companion object {
        fun from(title: String): SignalShape {
            return when (title) {
                "circle" -> CIRCLE
                "square" -> SQUARE
                "diamond" -> DIAMOND
                else -> CIRCLE
            }
        }
    }
}