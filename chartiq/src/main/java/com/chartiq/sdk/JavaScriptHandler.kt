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
        period: String?,
        interval: String?,
        start: String?,
        end: String?,
        meta: String?,
        callbackId: String?
    )

    @JavascriptInterface
    fun pullUpdate(
        symbol: String?,
        period: String?,
        interval: String?,
        start: String?,
        meta: String?,
        callbackId: String?
    )

    @JavascriptInterface
    fun pullPagination(
        symbol: String?,
        period: String?,
        interval: String?,
        start: String?,
        end: String?,
        meta: String?,
        callbackId: String?
    )
}
