package com.chartiq.demo

import com.chartiq.sdk.ChartIQ

interface ServiceLocator {
    val applicationPreferences: ApplicationPrefs
    val chartIQ: ChartIQ
}