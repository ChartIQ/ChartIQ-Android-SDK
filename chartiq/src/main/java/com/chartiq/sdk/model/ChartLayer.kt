package com.chartiq.sdk.model

/**
 * An enumeration of available chart layers
 */
enum class ChartLayer(
    /**
     * @suppress
     */
    val value: String
) {
    TOP("top"),
    UP("up"),
    BACK("down"),
    BOTTOM("bottom")
}
