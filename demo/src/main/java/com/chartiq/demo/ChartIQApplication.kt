package com.chartiq.demo

import android.app.Application
import android.content.pm.ApplicationInfo
import android.webkit.WebView
import com.chartiq.sdk.ChartIQ
import dev.b3nedikt.restring.Restring
import dev.b3nedikt.reword.RewordInterceptor
import dev.b3nedikt.viewpump.ViewPump

class ChartIQApplication : Application() {

    val chartIQ: ChartIQ by lazy {
        ChartIQ.getInstance(BuildConfig.DEFAULT_CHART_URL, this)
    }

    override fun onCreate() {
        super.onCreate()
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        Restring.init(this)
        ViewPump.init(RewordInterceptor)
    }
}
