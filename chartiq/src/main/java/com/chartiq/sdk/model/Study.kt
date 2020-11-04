package com.chartiq.sdk.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Study(
    val attributes: @RawValue Map<String, Any>?,
    val centerLine: Double,
    val customRemoval: Boolean,
    val deferUpdate: Boolean,
    val display: String?,
    var inputs: @RawValue Map<String, Any>?,
    var outputs: @RawValue Map<String, Any>?,
    val name: String,
    val overlay: Boolean,
    var parameters: @RawValue Map<String, Any>,
    val range: String?,
    val shortName: String,
    val type: String?,
    val underlay: Boolean,
    val yAxis: @RawValue Map<String, Any>?,
) : Parcelable

fun Study.splitName(): Pair<String, String>{
    val result = name.split("\u200C")
    return when (result.size) {
        3 -> Pair(result[1], result[2])
        2 -> Pair(result.first(), result.last())
        else -> Pair(result.toString(), "")
    }
}
