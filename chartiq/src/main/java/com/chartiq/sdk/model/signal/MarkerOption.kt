package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Optional settings for main series marker. When multiple conditions match, markerOptions from the first matching condition are applied.
 *
 * @property signalShape of the signal marker on the chart. The shape of the marker on the study will always be "circle". If omitted, "circle" assumed.".
 * @property signalSize of the signal marker on the chart. Possible values are S/M/L. The size of the marker on the study will always be S. If omitted, "S" assumed.
 * @property label Optional string to display in the marker."
 * @property signalPosition Where to display the signal as a marker in relation to the main plot. If omitted, "above_candle" assumed.
 */

@Parcelize
data class MarkerOption(
    val signalShape: SignalShape,
    val signalSize: SignalSize,
    val label: String,
    val signalPosition: SignalPosition,
) : Parcelable

fun MarkerOption.toData(): MarkerOptionEntity{
    return MarkerOptionEntity(
        shape = when (signalShape) {
            SignalShape.CIRCLE -> "circle"
            SignalShape.SQUARE -> "square"
            SignalShape.DIAMOND -> "diamond"
        },
        size = when (signalSize) {
            SignalSize.S -> "S"
            SignalSize.M -> "M"
            SignalSize.L -> "L"
        },
        label = label,
        position = when(signalPosition){
            SignalPosition.ABOVE_CANDLE -> "above_candle"
            SignalPosition.BELOW_CANDLE -> "below_candle"
            SignalPosition.ON_CANDLE -> "on_candle"
        }
    )
}