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
    val shape: String,
    val size: String,
    val label: String,
    val position: String,
) : Parcelable