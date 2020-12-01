package com.chartiq.demo.ui.settings.chartstyle

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChartTypeModel(
    val title: String,
    val name: String,
    @DrawableRes val iconRes: Int
):Parcelable
