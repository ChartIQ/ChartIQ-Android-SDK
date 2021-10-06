package com.chartiq.sdk

/**
 * Functional interface that represents a callback mostly for all requests to ChartIQ SDK
 */
fun interface OnReturnCallback<T> {
    fun onReturn(result: T)
}
