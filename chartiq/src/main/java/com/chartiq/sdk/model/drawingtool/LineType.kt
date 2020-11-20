package com.chartiq.sdk.model.drawingtool

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class LineType(val value: String) : Parcelable {
    @SerializedName("solid")
    SOLID("solid"),

    @SerializedName("dotted")
    DOTTED("dotted"),

    @SerializedName("dashed")
    DASHED("dashed")
}
