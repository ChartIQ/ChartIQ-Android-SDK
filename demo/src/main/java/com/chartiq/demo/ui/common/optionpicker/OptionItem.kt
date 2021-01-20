package com.chartiq.demo.ui.common.optionpicker

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OptionItem(
    val value: String,
    val isSelected: Boolean
) : Parcelable
