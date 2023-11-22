package com.chartiq.sdk.model.signal

import android.os.Parcelable
import com.chartiq.sdk.model.study.Study
import kotlinx.android.parcel.Parcelize
import java.net.URLEncoder

/**
 * Encapsulates parameters with additional information for Study. ChartIQ uses the term “study” to refer to any indicator, oscillator, average, or signal that results from technical analysis of chart data.
 ** @property name Name of Signal. Signal will be saved with this name and this name will appear in any study legend and in the expanded signal's title.
 * @property conditions Array of conditions; each condition is itself an array of [lhs, operator, rhs, color]
 * @property joiner & or | to join conditions. If omitted, "|" assumed.
 * @property description Description of signal.

 */
@Parcelize
data class Signal(
    val uniqueId: String,
    val name: String,
    val conditions: List<Condition>,
    val joiner: SignalJoiner,
    val description: String,
    val disabled: Boolean,
    val study: Study
) : Parcelable

fun Signal.toData(): SignalEntity {
    return SignalEntity(
        uniqueId = uniqueId,
        signalName = URLEncoder.encode(name, "UTF-8"),
        conditions = conditions.map { condition -> condition.toData() },
        joiner = when (joiner) {
            SignalJoiner.OR -> "|"
            SignalJoiner.AND -> "&"
        },
        description = URLEncoder.encode(description,"UTF-8"),
        disabled = disabled,
        studyName = study.shortName,
        studyEntity = null
    )
}
