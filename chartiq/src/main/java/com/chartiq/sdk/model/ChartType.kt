package com.chartiq.sdk.model

enum class ChartType(val value: String) {
    CANDLE("candle"),
    BAR("bar"),
    COLORED_BAR("coloredBar"),
    LINE("line"),
    VERTEX_LINE("vertexLine"),
    STEP("step"),
    MOUNTAIN("mountain"),
    BASELINE("baseline"),
    HOLLOW_CANDLE("hollowCandle"),
    VOLUME_CANDLE("volumeCandle"),
    COLORED_HLCBAR("coloredHlcbar"),
    SCATTERPLOT("scatterplot"),
    HISTOGRAM("histogram")
}
