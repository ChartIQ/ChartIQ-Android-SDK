package com.chartiq.demo.ui.settings.chartstyle

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChartTypeItem(
    val title: String,
    val name: String,
    @DrawableRes val iconRes: Int,
    val isSelected: Boolean = false
) : Parcelable
