package com.chartiq.sdk.model.study

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Encapsulates parameters with additional information for Study. ChartIQ uses the term “study” to refer to any indicator, oscillator, average, or signal that results from technical analysis of chart data.
 *
 * @property name The study's ID. Includes ZWNJ characters. Please note: To facilitate study name translations, study names use zero-width non-joiner (unprintable) characters to delimit the general study name from the specific study parameters. Example: "\u200c"+"Aroon"+"\u200c"+" (14)".
 * @property attributes Attributes of  the study
 * @property centerLine A center line od the study
 * @property customRemoval A custom removal of the study
 * @property deferUpdate A defer update of the study
 * @property display A display name of the study
 * @property inputs Names and values of input fields
 * @property outputs Names and values (colors) of outputs
 * @property parameters Additional parameters that are unique to the particular study
 * @property range A range of the study
 * @property shortName A shortName of the study
 * @property type The type of study, which can be used as a look up in the StudyLibrary
 * @property underlay Determines whether the study is an underlay or overlay
 * @property yAxis Y-Axis value of the study
 * @property signalIQExclude Determines whether the study should be excluded from SignalIQ
 */
@Parcelize
data class Study(
    val name: String,
    val attributes: @RawValue Map<String, Any>?,
    val centerLine: Double,
    val customRemoval: Boolean,
    val deferUpdate: Boolean,
    val display: String?,
    var inputs: @RawValue Map<String, Any>?,
    var outputs: @RawValue Map<String, Any>?,
    val overlay: Boolean,
    var parameters: @RawValue Map<String, Any>,
    val range: String?,
    val shortName: String,
    val type: String?,
    val underlay: Boolean,
    val yAxis: @RawValue Map<String, Any>?,
    val signalIQExclude: Boolean
) : Parcelable
