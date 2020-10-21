package com.chartiq.demo

import android.app.Application
import com.chartiq.sdk.ChartIQHandler

class ChartIQApplication : Application() {

    val chartIQHandler: ChartIQHandler by lazy {
        ChartIQHandler()
    }
}