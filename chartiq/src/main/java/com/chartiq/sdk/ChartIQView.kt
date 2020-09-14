package com.chartiq.sdk

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import com.chartiq.sdk.model.*
import com.chartiq.sdk.scriptmanager.ChartIQScriptManager
import com.google.gson.Gson

class ChartIQView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WebView(context, attrs), ChartIQ, JavaScriptHandler {

    private var dataSource: DataSource? = null
    private val scriptManager = ChartIQScriptManager()
    private var parameters = HashMap<String, Boolean>()
    private val accessibilityManager: AccessibilityManager by lazy {
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    }

    override fun start(chartIQUrl: String, onStartCallback: OnStartCallback) {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        addJavascriptInterface(this@ChartIQView, JAVASCRIPT_INTERFACE_QUOTE_FEED)
        addJavascriptInterface(this@ChartIQView, JAVASCRIPT_INTERFACE_PARAMETERS)
        loadUrl(chartIQUrl)
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                executeJavascript(scriptManager.getDetermineOSScript())
                executeJavascript(scriptManager.getNativeQuoteFeedScript())
                executeJavascript(scriptManager.getAddDrawingListenerScript())
                executeJavascript(scriptManager.getAddLayoutListenerScript())

                onStartCallback.onStart()
            }
        }
    }

    @JavascriptInterface
    override fun layoutChange(json: String) = Unit

    @JavascriptInterface
    override fun drawingChange(json: String) = Unit

    @JavascriptInterface
    override fun pullInitialData(
        symbol: String?,
        period: Int?,
        interval: String?,
        start: String?,
        end: String?,
        meta: Any?,
        callbackId: String?
    ) {
        val quoteFeedParams =
            QuoteFeedParams(symbol, period, interval, start, end, meta, callbackId)
        dataSource?.pullInitialData(quoteFeedParams) { data ->
            callbackId?.let {
                invokePullCallback(callbackId, data)
            }
        }
    }

    @JavascriptInterface
    override fun pullUpdate(
        symbol: String?,
        period: Int?,
        interval: String?,
        start: String?,
        meta: Any?,
        callbackId: String?
    ) {
        val quoteFeedParams =
            QuoteFeedParams(symbol, period, interval, start, null, meta, callbackId)
        dataSource?.pullUpdateData(quoteFeedParams) { data ->
            callbackId?.let {
                invokePullCallback(callbackId, data)
            }
        }
    }

    @JavascriptInterface
    override fun pullPagination(
        symbol: String?,
        period: Int?,
        interval: String?,
        start: String?,
        end: String?,
        meta: Any?,
        callbackId: String?
    ) {
        val quoteFeedParams =
            QuoteFeedParams(symbol, period, interval, start, end, meta, callbackId)
        dataSource?.pullPaginationData(quoteFeedParams) { data ->
            callbackId?.let {
                invokePullCallback(callbackId, data)
            }
        }
    }

    override fun setSymbol(symbol: String) {
        if (accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled) {
            executeJavascript(scriptManager.getSetAccessibilityModeScript())
        }
        executeJavascript(scriptManager.getDateFromTickScript())
        executeJavascript(scriptManager.getSetSymbolScript(symbol))
    }

    override fun setDataSource(dataSource: DataSource) {
        this.dataSource = dataSource
    }

    override fun setDataMethod(method: DataMethod, symbol: String) {
        when (method) {
            DataMethod.PUSH -> executeJavascript(scriptManager.getSetDataMethodScript(symbol))
            DataMethod.PULL -> Log.d(
                javaClass.simpleName,
                "If you want to add a QuoteFeed please do so in your javascript code."
            )
        }
    }

    override fun enableCrosshairs() {
        executeJavascript(scriptManager.getEnableCrosshairScript(true))
    }

    override fun disableCrosshairs() {
        executeJavascript(scriptManager.getEnableCrosshairScript(false))
    }

    override fun setPeriodicity(period: Int, interval: String, timeUnit: String) {
        executeJavascript(scriptManager.getSetPeriodicityScript(period, interval, timeUnit))
    }

    override fun enableDrawing(type: DrawingTool) {
        executeJavascript(scriptManager.getEnableDrawingScript(type))
    }

    override fun disableDrawing() {
        executeJavascript(scriptManager.getDisableDrawingScript())
    }

    override fun clearDrawing() {
        executeJavascript(scriptManager.getClearDrawingScript())
    }

    override fun getStudyList(callback: OnReturnCallback<List<Study>>) {
        executeJavascript(scriptManager.getGetStudyListScript()) { value ->
            callback.onReturn(Gson().fromJson(value, Array<Study>::class.java).toList())
        }
    }

    override fun getActiveStudies(callback: OnReturnCallback<List<Study>>) {
        executeJavascript(scriptManager.getGetActiveStudiesScript()) { value ->
            val result = if (value.toLowerCase() == "null") {
                "[]";
            } else {
                value
            }

            callback.onReturn(Gson().fromJson(result, Array<Study>::class.java).toList())
        }
    }

    override fun setAggregationType(aggregationType: AggregationType) {
        executeJavascript(scriptManager.getSetAggregationTypeScript(aggregationType))
    }

    override fun setChartType(chartType: ChartType) {
        executeJavascript(scriptManager.getSetChartTypeScript(chartType.value))
    }

    override fun setChartScale(scale: ChartScale) {
        executeJavascript(scriptManager.getSetChartScaleScript(scale.value))
    }

    override fun removeStudy(studyName: String) {
        executeJavascript(scriptManager.getRemoveStudyScript(studyName))
    }

    override fun addStudy(study: Study, firstLoad: Boolean) {
        var inputs = study.inputs
        var outputs = study.outputs
        val params = study.parameters
        if (firstLoad) {
            inputs = null
            outputs = null
        }
        val scripts = study.type?.run {
            scriptManager.getAddStudyScript(study.type, inputs, outputs, params)
        } ?: scriptManager.getAddStudyScript(study.shortName, inputs, outputs, params)
        executeJavascript(scripts)
    }

    override fun setDrawingParameter(parameter: DrawingParameter, value: String) {
        executeJavascript(scriptManager.getSetDrawingParameterScript(parameter.value, value))
    }

    override fun setOHLCParameters(params: HashMap<String, Boolean>) {
        parameters = params
    }

    override fun getStudyInputParameters(studyName: String, callback: OnReturnCallback<String>) {
        executeJavascript(scriptManager.getGetStudyInputParametersScript(studyName)) { value ->
            callback.onReturn(value)
        }
    }

    override fun getStudyOutputParameters(studyName: String, callback: OnReturnCallback<String>) {
        executeJavascript(scriptManager.getGetStudyOutputParametersScript(studyName)) { value ->
            callback.onReturn(value)
        }
    }

    override fun getStudyParameters(studyName: String, callback: OnReturnCallback<String>) {
        executeJavascript(scriptManager.getGetStudyParametersScript(studyName)) { value ->
            callback.onReturn(value)
        }
    }

    private fun executeJavascript(script: String, callback: ValueCallback<String>? = null) {
        evaluateJavascript(script, callback)
    }

    private fun invokePullCallback(callbackId: String, data: List<OHLCParams>) {
        executeJavascript(scriptManager.getParseDataScript(data, callbackId))
    }

    companion object {
        private const val JAVASCRIPT_INTERFACE_QUOTE_FEED = "QuoteFeed"
        private const val JAVASCRIPT_INTERFACE_PARAMETERS = "parameters"
    }
}
