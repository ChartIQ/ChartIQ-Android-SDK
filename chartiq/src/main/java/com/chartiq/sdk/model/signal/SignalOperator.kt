package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class SignalOperator(val title: String) : Parcelable {
    GREATER_THAN("Grater than"),
    LESS_THAN("Less than"),
    EQUAL_TO("Equal to"),
    CROSSES("Crosses"),
    CROSSES_ABOVE("Crosses above"),
    CROSSES_BELOW("Crosses below"),
    TURNS_UP("Turns up"),
    TURNS_DOWN("Turns down"),
    INCREASES("Increases"),
    DECREASES("Decreases"),
    DOES_NOT_CHANGE("Does not change")
}