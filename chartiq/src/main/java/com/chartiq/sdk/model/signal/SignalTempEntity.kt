package com.chartiq.sdk.model.signal

import android.graphics.drawable.shapes.Shape
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Encapsulates parameters with additional information for Study. ChartIQ uses the term “study” to refer to any indicator, oscillator, average, or signal that results from technical analysis of chart data.
 *
 * @property notificationType Type of notification for the signal. At this time the only defined value is marker.".
 * @property name Name of Signal. Signal will be saved with this name and this name will appear in any study legend and in the expanded signal's title.
 * @property conditions Array of conditions; each condition is itself an array of [lhs, operator, rhs, color]
 * @property joiner & or | to join conditions. If omitted, "|" assumed.
 * @property results Signals indicating where conditions were met.
 * @property description Description of signal.
 * @property reveal Whether to display the study. If omitted, "false" assumed.
 * @property position Where to display the signal as a marker in relation to the main plot. See CIQ.Marker for options. If omitted, "above_candle" assumed.
 * @property shape Shape of the signal marker on the chart. The shape of the marker on the study will always be "circle". If omitted, "circle" assumed.
 * @property size Size of the signal marker on the chart. Possible values are S/M/L. The size of the marker on the study will always be S. If omitted, "S" assumed.
 * @property panelHeight Number of pixels in a panel study when revealed. Defaults to plugin config's panelHeight.
 * @property label Optional string to display in the marker.
 * @property destination Optional delivery point of alert.
 */
@Parcelize
data class SignalTempEntity(
    val name: String,
    val conditions: List<ConditionEntity>,
    val joiner: String,
    val description: String,
    val shape: String,
    val size: String,
    val label: String,
) : Parcelable
