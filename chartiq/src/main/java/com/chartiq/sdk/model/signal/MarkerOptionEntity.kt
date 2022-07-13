package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Optional settings for main series marker. When multiple conditions match, markerOptions from the first matching condition are applied.
 *
 * @property shape of the signal marker on the chart. The shape of the marker on the study will always be "circle". If omitted, "circle" assumed.".
 * @property signalSize of the signal marker on the chart. Possible values are S/M/L. The size of the marker on the study will always be S. If omitted, "S" assumed.
 * @property label Optional string to display in the marker."
 * @property signalPosition Where to display the signal as a marker in relation to the main plot. If omitted, "above_candle" assumed.
 */

@Parcelize
data class MarkerOptionEntity(
    val type: String,
    val shape: String,
    val size: String,
    val label: String,
    val position: String,
) : Parcelable

fun MarkerOptionEntity.toMarkerOption(color: String?): MarkerOption {
    return MarkerOption(
        type = SignalMarkerType.from(type),
        signalShape = SignalShape.from(shape),
        signalSize = SignalSize.from(size),
        label = label,
        signalPosition = SignalPosition.from(position),
        color = color
    )
}

fun markerOptionOnlyWithColor(color: String?): MarkerOption {
    return MarkerOption(
        type = SignalMarkerType.from(""),
        signalShape = SignalShape.from(""),
        signalSize = SignalSize.from(""),
        label = "",
        signalPosition = SignalPosition.from(""),
        color = color
    )
}