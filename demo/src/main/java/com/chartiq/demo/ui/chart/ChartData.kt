package com.chartiq.demo.ui.chart

import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.OHLCParams

data class ChartData(
    val data: List<OHLCParams>,
    val callback: DataSourceCallback
)
