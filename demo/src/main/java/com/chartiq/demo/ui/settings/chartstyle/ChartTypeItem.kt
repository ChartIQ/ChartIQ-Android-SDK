package com.chartiq.demo.ui.settings.chartstyle

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChartTypeItem(
    @StringRes val titleRes: Int,
    val name: String,
    @DrawableRes val iconRes: Int,
    val isSelected: Boolean = false
) : Parcelable
