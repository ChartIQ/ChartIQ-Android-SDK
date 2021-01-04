package com.chartiq.demo

import android.app.Application
import android.content.pm.ApplicationInfo
import android.webkit.WebView
import com.chartiq.demo.localization.ChartIQLocalizationManager
import com.chartiq.demo.localization.LocalizationManager
import com.chartiq.sdk.ChartIQ


class ChartIQApplication : Application(), ServiceLocator {

    override val applicationPreferences: ApplicationPrefs by lazy {
        ApplicationPrefs.Default(this)
    }
    override val localizationManager: LocalizationManager by lazy {
        ChartIQLocalizationManager()
    }
    override val chartIQ: ChartIQ by lazy {
        ChartIQ.getInstance(BuildConfig.DEFAULT_CHART_URL, this)
    }

    override fun onCreate() {

        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        localizationManager.init(this)
        super.onCreate()
    }
}
