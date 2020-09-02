package com.chartiq.sdk

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import com.chartiq.sdk.JavaScriptHandler.Companion.PARAM_END
import com.chartiq.sdk.JavaScriptHandler.Companion.PARAM_INTERVAL
import com.chartiq.sdk.JavaScriptHandler.Companion.PARAM_PERIOD
import com.chartiq.sdk.JavaScriptHandler.Companion.PARAM_START
import com.chartiq.sdk.JavaScriptHandler.Companion.PARAM_SYMBOL
import com.chartiq.sdk.model.*
import com.chartiq.sdk.scriptmanager.ChartIQScriptManager
import com.google.gson.Gson
import java.util.Map

class ChartIQView : WebView, ChartIQ, JavaScriptHandler {

    companion object {
        private const val JAVASCRIPT_INTERFACE_QUOTE_FEED = "QuoteFeed"
    }

    private var dataSource: DataSource? = null
    private val scriptManager = ChartIQScriptManager()
    private var parameters = HashMap<String, Boolean>()
    private val accessibilityManager: AccessibilityManager by lazy {
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun start(chartIQUrl: String, onStartCallback: OnStartCallback) {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        addJavascriptInterface(this@ChartIQView, JAVASCRIPT_INTERFACE_QUOTE_FEED)
        addJavascriptInterface(this@ChartIQView, "parameters")
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
    override fun layoutChange(json: String) {
        TODO("Not yet implemented")
    }

    @JavascriptInterface
    override fun drawingChange(json: String) {
        TODO("Not yet implemented")
    }

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
        val params = java.util.HashMap<String, Object>().apply {
            put(PARAM_SYMBOL, symbol as Object)
            put(PARAM_PERIOD, period.toString() as Object)
            put(PARAM_INTERVAL, interval as Object)
            put(PARAM_START, start as Object)
            put(PARAM_END, end as Object)
        }
        dataSource?.pullInitialData(params as Map<String, Object>, object : DataSourceCallback {
            override fun execute(data: Array<OHLCParams>) {
                callbackId?.let {
                    invokePullCallback(callbackId, data)
                }
            }
        })
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
        val params = java.util.HashMap<String, Object>().apply {
            put(PARAM_SYMBOL, symbol as Object)
            put(PARAM_PERIOD, period.toString() as Object)
            put(PARAM_INTERVAL, interval as Object)
            put(PARAM_START, start as Object)
        }
        dataSource?.pullInitialData(params as Map<String, Object>, object : DataSourceCallback {
            override fun execute(data: Array<OHLCParams>) {
                callbackId?.let {
                    invokePullCallback(callbackId, data)
                }
            }
        })
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
        val params = java.util.HashMap<String, Object>().apply {
            put(PARAM_SYMBOL, symbol as Object)
            put(PARAM_PERIOD, period.toString() as Object)
            put(PARAM_INTERVAL, interval as Object)
            put(PARAM_START, start as Object)
            put(PARAM_END, end as Object)
        }
        dataSource?.pullInitialData(params as Map<String, Object>, object : DataSourceCallback {
            override fun execute(data: Array<OHLCParams>) {
                callbackId?.let {
                    invokePullCallback(callbackId, data)
                }
            }
        })
    }

    override fun setSymbol(symbol: String) {
        if (accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled) {
            executeJavascript(scriptManager.getSetAccessibilityModeScript())
        }
        executeJavascript(scriptManager.getSetDataMethodScript(symbol))
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
                "If you want to add a quotefeed please do so in your javascript code."
            )
        }
    }

    override fun enableCrosshairs() {
        executeJavascript(scriptManager.getEnableCrosshairsScript())
    }

    override fun disableCrosshairs() {
        executeJavascript(scriptManager.getDisableCrosshairsScript());
    }

    override fun setPeriodicity(period: Int, interval: String, timeUnit: String) {
        executeJavascript(scriptManager.getSetPeriodicityScript(period, interval, timeUnit));
    }

    override fun enableDrawing(type: DrawingTool) {
        executeJavascript(scriptManager.getEnableDrawingScript(type));
    }

    override fun disableDrawing() {
        executeJavascript(scriptManager.getDisableDrawingScript());
    }

    override fun clearDrawing() {
        executeJavascript(scriptManager.getClearDrawingScript());
    }

    override fun getStudyList(callback: OnReturnCallback<Array<Study>>) {
        executeJavascript(
            scriptManager.getGetStudyListScript(),
            ValueCallback { value ->
                callback.onReturn(
                    Gson().fromJson(
                        value,
                        Array<Study>::class.java
                    )
                )
            })
    }

    override fun getActiveStudies(callback: OnReturnCallback<Array<Study>>) {
        executeJavascript(scriptManager.getGetActiveStudiesScript(),
            ValueCallback { value ->
                val result =
                    if (value.toLowerCase() == "null") {
                        "[]";
                    } else {
                        value
                    }

                callback.onReturn(Gson().fromJson(result, Array<Study>::class.java));
            })
    }

    override fun setAggregationType(aggregationType: AggregationType) {
        executeJavascript(scriptManager.getSetAggregationTypeScript(aggregationType));
    }

    override fun setChartType(chartType: String) {
        executeJavascript(scriptManager.getSetChartTypeScript(chartType));
    }

    override fun setChartScale(scale: String) {
        executeJavascript(scriptManager.getSetChartScaleScript(scale));
    }

    override fun removeStudy(studyName: String) {
        executeJavascript(scriptManager.getRemoveStudyScript(studyName));
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
            scriptManager.getAddStudyScript(study.type, inputs, outputs, params);
        } ?: scriptManager.getAddStudyScript(study.shortName, inputs, outputs, params)
        executeJavascript(scripts)
    }

    override fun setDrawingParameter(parameter: String, value: String) {
        executeJavascript(scriptManager.getSetDrawingParameterScript(parameter, value), null);
    }

    // TODO: 02.09.20 find out the need of the following method
    override fun setTalkbackFields(talkbackFields: HashMap<String, Boolean>) {
        parameters = talkbackFields;
    }

    override fun getStudyInputParameters(studyName: String, callback: OnReturnCallback<String>) {
        executeJavascript(scriptManager.getGetStudyInputParametersScript(studyName),
            ValueCallback { value -> callback.onReturn(value) })
    }

    override fun getStudyOutputParameters(studyName: String, callback: OnReturnCallback<String>) {
        executeJavascript(scriptManager.getGetStudyOutputParametersScript(studyName),
            ValueCallback { value -> callback.onReturn(value) })
    }

    override fun getStudyParameters(studyName: String, callback: OnReturnCallback<String>) {
        executeJavascript(scriptManager.getGetStudyParametersScript(studyName),
            ValueCallback { value -> callback.onReturn(value) })
    }

    private fun executeJavascript(script: String, callback: ValueCallback<String>? = null) {
        evaluateJavascript(script, callback)
    }

    private fun invokePullCallback(callbackId: String, data: Array<OHLCParams>) {
        executeJavascript(scriptManager.getParseDataScript(data, callbackId))
    }
}
