package com.chartiq.sdk

import android.webkit.JavascriptInterface

interface JavaScriptHandler {

    @JavascriptInterface
    fun layoutChange(json: String)

    @JavascriptInterface
    fun drawingChange(json: String)

    @JavascriptInterface
    fun pullInitialData(
        symbol: String?,
        period: Int?,
        interval: String?,
        start: String?,
        end: String?,
        meta: Any?,
        callbackId: String?
    )

    @JavascriptInterface
    fun pullUpdate(
        symbol: String?,
        period: Int?,
        interval: String?,
        start: String?,
        meta: Any?,
        callbackId: String?
    )

    @JavascriptInterface
    fun pullPagination(
        symbol: String?,
        period: Int?,
        interval: String?,
        start: String?,
        end: String?,
        meta: Any?,
        callbackId: String?
    )

    companion object {
        const val PARAM_SYMBOL = "symbol"
        const val PARAM_PERIOD = "period"
        const val PARAM_INTERVAL = "interval"
        const val PARAM_START = "start"
        const val PARAM_END = "end"
        const val PARAM_META = "meta"
    }
}
