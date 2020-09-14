package com.chartiq.sdk

import com.chartiq.sdk.model.OHLCParams

fun interface DataSourceCallback {
    fun execute(data: List<OHLCParams>)
}
