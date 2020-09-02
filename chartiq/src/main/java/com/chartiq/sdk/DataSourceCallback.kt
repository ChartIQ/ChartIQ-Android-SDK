package com.chartiq.sdk

import com.chartiq.sdk.model.OHLCParams

interface DataSourceCallback {
    fun execute(data: Array<OHLCParams>)
}
