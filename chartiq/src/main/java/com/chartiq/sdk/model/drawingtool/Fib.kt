package com.chartiq.sdk.model.drawingtool

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Fib(
    @SerializedName("level")
    val level: Double,
    @SerializedName("display")
    val display: Boolean
) : Parcelable
