package com.chartiq.sdk.model

/**
 * A series is a set of data, for example a line graph or one set of columns. All data plotted on a chart comes from the series object.
 * @property symbolName A string that contains series's symbol
 * @property color A string with a hex color of the symbol to be displayed
 */
data class Series(
    val symbolName: String,
    val color: String
)
