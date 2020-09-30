package com.chartiq.demo.ui.chart

import com.chartiq.sdk.model.OHLCParams

@Deprecated("Callback is used by TemporaryAsyncTask which will be removed soon")
fun interface Callback {
    fun setOHLCChartData(data: List<OHLCParams>)
}
