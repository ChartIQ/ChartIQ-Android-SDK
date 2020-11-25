package com.chartiq.sdk.model

enum class AggregationType(val value: String) {
    HEIKINASHI("Heikin Ashi"),
    KAGI("Kagi"),
    LINEBREAK("Line Break"),
    RENKO("Renko"),
    RANGEBARS("Range Bars"),
    PANDF("Point & Figure"),
    OHLC("OHLC")
}
