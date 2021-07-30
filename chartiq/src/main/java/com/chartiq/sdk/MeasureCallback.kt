package com.chartiq.sdk

/**
 * Functional interface that represents a callback for [ChartIQ.addMeasureListener]
 */
fun interface MeasureCallback {

    fun onMeasureUpdate(update: String)
}
