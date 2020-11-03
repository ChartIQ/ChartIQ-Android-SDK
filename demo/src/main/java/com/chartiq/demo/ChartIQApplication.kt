package com.chartiq.demo

import android.app.Application
import android.content.pm.ApplicationInfo
import android.webkit.WebView
import com.chartiq.sdk.ChartIQHandler

class ChartIQApplication : Application() {

    val chartIQHandler: ChartIQHandler by lazy {
        ChartIQHandler(BuildConfig.DEFAULT_CHART_URL, this)
    }

    override fun onCreate() {
        super.onCreate()
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }
}