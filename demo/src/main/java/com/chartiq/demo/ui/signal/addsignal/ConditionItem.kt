package com.chartiq.demo.ui.signal.addsignal

import android.os.Parcelable
import com.chartiq.sdk.model.signal.Condition
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConditionItem(
    val condition: Condition,
    val title: String,
    val description: String,
    val displayedColor: String,
    val UUID: java.util.UUID = java.util.UUID.randomUUID()
) : Parcelable