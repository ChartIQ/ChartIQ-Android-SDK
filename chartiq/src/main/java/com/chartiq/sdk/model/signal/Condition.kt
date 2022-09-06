package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Encapsulates parameters with additional information for Study. ChartIQ uses the term “study” to refer to any indicator, oscillator, average, or signal that results from technical analysis of chart data.
 *
 * @property leftIndicator is a field in the study's outputMap ".
 * @property rightIndicator can be either a numeric value or a field in the study's outputMap
 * @property signalOperator can be "<", "<=", "=", ">", ">=", "<>", ">p" (greater than previous), "<p" (less than previous), "=p" (same as previous), "x" (crosses another plot/value in either direction), "x+" (crosses another plot/value upwards", "x-" (crosses another plot/value downwards"
 * @property color is the color of the signal. If not provided, lhs's color will be used
 */
@Parcelize
data class Condition(
    val leftIndicator: String,
    val rightIndicator: String?,
    val signalOperator: SignalOperator,
    val markerOption: MarkerOption
) : Parcelable

fun Condition.toData(): ConditionEntity {
    return ConditionEntity(
        leftIndicator = leftIndicator,
        rightIndicator = rightIndicator,
        signalOperator = when (signalOperator) {
            SignalOperator.GREATER_THAN -> ">"
            SignalOperator.LESS_THAN -> "<"
            SignalOperator.EQUAL_TO -> "="
            SignalOperator.CROSSES -> "x"
            SignalOperator.CROSSES_ABOVE -> "x+"
            SignalOperator.CROSSES_BELOW -> "x-"
            SignalOperator.TURNS_UP -> "t+"
            SignalOperator.TURNS_DOWN -> "t-"
            SignalOperator.INCREASES -> ">p"
            SignalOperator.DECREASES -> "<p"
            SignalOperator.DOES_NOT_CHANGE -> "=p"
        },
        color = markerOption.color,
        markerOption = markerOption.toData()
    )
}
