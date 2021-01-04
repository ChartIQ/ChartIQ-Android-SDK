package com.chartiq.sdk

import android.webkit.JavascriptInterface

internal interface JavaScriptHandler {

    @JavascriptInterface
    fun layoutChange(json: String)

    @JavascriptInterface
    fun drawingChange(json: String)

    @JavascriptInterface
    fun measureChange(json: String)

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
}
