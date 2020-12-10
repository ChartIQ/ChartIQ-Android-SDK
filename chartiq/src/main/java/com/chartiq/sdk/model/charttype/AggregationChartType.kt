package com.chartiq.sdk.model.charttype

enum class AggregationChartType(val value: String) {
    HEIKINASHI("Heikin Ashi"),
    KAGI("Kagi"),
    LINEBREAK("Line Break"),
    RENKO("Renko"),
    RANGEBARS("Range Bars"),
    PANDF("Point & Figure")
}
