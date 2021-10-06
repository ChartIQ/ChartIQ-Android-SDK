package com.chartiq.sdk

import com.chartiq.sdk.model.OHLCParams

/**
 * Functional interface that represents a callback for [DataSource] data source
 */
fun interface DataSourceCallback {
    fun execute(data: List<OHLCParams>)
}
