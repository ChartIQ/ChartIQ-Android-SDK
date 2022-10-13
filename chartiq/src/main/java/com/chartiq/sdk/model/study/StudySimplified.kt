package com.chartiq.sdk.model.study

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Encapsulates parameters with additional information for Study. ChartIQ uses the term “study” to refer to any indicator, oscillator, average, or signal that results from technical analysis of chart data.
 *
 * @property studyName The study's ID. Includes ZWNJ characters. Please note: To facilitate study name translations, study names use zero-width non-joiner (unprintable) characters to delimit the general study name from the specific study parameters. Example: "\u200c"+"Aroon"+"\u200c"+" (14)".
 * @property outputs Names and values (colors) of outputs
 * @property type The type of study, which can be used as a look up in the StudyLibrary
 */
@Parcelize
data class StudySimplified(
    val studyName: String,
    var outputs: @RawValue Map<String, Any>?,
    val type: String?,
) : Parcelable
