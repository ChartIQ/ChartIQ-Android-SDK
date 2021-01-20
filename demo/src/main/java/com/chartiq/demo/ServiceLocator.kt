package com.chartiq.demo

import com.chartiq.demo.localization.LocalizationManager
import com.chartiq.sdk.ChartIQ

interface ServiceLocator {
    val applicationPreferences: ApplicationPrefs
    val localizationManager: LocalizationManager
    val chartIQ: ChartIQ
}
