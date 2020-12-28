package com.chartiq.sdk.model.study

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Encapsulates parameters with additional information for Study.
 */
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
    val yAxis: @RawValue Map<String, Any>?
) : Parcelable
