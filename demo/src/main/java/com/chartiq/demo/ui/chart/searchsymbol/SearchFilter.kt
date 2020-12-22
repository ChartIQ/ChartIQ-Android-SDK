package com.chartiq.demo.ui.chart.searchsymbol

enum class SearchFilter(val value: String?) {
    ALL(null),
    STOCKS("STOCKS"),
    FX("FOREX"),
    INDEXES("INDEXES"),
    FUNDS("FUNDS"),
    FUTURES("FUTURES")
}
