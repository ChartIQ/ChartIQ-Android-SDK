package com.chartiq.sdk.model.charttype

enum class ChartType(val value: String) {
    CANDLE("Candle"),
    BAR("Bar"),
    COLORED_BAR("Colored Bar"),
    LINE("Line"),
    VERTEX_LINE("Vertex Line"),
    STEP("Step"),
    MOUNTAIN("Mountain"),
    BASELINE("Baseline"),
    HOLLOW_CANDLE("Hollow Candle"),
    VOLUME_CANDLE("Volume Candle"),
    COLORED_HLC("Colored HLC Bar"),
    SCATTERPLOT("Scatterplot"),
    HISTOGRAM("Histogram")
}
