package com.chartiq.sdk

fun interface ChartAvailableCallback {

    fun onChartAvailableUpdate(isChartAvailable: Boolean)
}