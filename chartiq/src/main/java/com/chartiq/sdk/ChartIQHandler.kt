package com.chartiq.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.*
import com.chartiq.sdk.adapters.StudyEntityClassTypeAdapter
import com.chartiq.sdk.model.*
import com.chartiq.sdk.scriptmanager.ChartIQScriptManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*


@SuppressLint("SetJavaScriptEnabled")
class ChartIQHandler(
    private val chartIQUrl: String,
    context: Context,
) : ChartIQ, JavaScriptHandler {
    private var dataSource: DataSource? = null
    private val scriptManager = ChartIQScriptManager()
    private var parameters = HashMap<String, Boolean>()
    val chartIQView = ChartIQView(context)

    init {
        chartIQView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }
            addJavascriptInterface(this@ChartIQHandler, JAVASCRIPT_INTERFACE_QUOTE_FEED)
            addJavascriptInterface(this@ChartIQHandler, JAVASCRIPT_INTERFACE_PARAMETERS)
            loadUrl(chartIQUrl)
        }
    }

    override fun start(onStartCallback: OnStartCallback) {
        chartIQView.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    executeJavascript(scriptManager.getDetermineOSScript())
                    executeJavascript(scriptManager.getNativeQuoteFeedScript())
                    executeJavascript(scriptManager.getAddDrawingListenerScript())
                    executeJavascript(scriptManager.getAddLayoutListenerScript())
                    executeJavascript(scriptManager.getAddMeasureListener())
                    onStartCallback.onStart()
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    Log.d(TAG, consoleMessage?.message() ?: "Undefined JS exception")
                    return super.onConsoleMessage(consoleMessage)
                }
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
        if (chartIQView.accessibilityManager.isEnabled && chartIQView.accessibilityManager.isTouchExplorationEnabled) {
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
        executeJavascript(scriptManager.getGetStudyListScript()) { value: String ->
            val result = if (value.toLowerCase(Locale.ENGLISH) == "null") {
                "[]"
            } else {
                value
            }
            val objectResult = Gson().fromJson(result, Object::class.java)
            val typeToken = object : TypeToken<Map<String, StudyEntity>>() {}.type
            val studyList = Gson().fromJson<Map<String, StudyEntity>>(
                objectResult.toString(), typeToken
            ).map { (key, value) ->
                value.copy(shortName = key)
                    .toStudy()
            }
            callback.onReturn(studyList)
        }
    }

    override fun getActiveStudies(callback: OnReturnCallback<List<Study>>) {
        executeJavascript(scriptManager.getGetActiveStudiesScript()) { value ->
            val result = if (value.toLowerCase(Locale.ENGLISH) == "null") {
                "[]"
            } else {
                value
            }
            val typeToken = object : TypeToken<List<StudyEntity>>() {}.type

            val gson = GsonBuilder()
                .registerTypeAdapter(StudyEntity::class.java, StudyEntityClassTypeAdapter())
                .create()
            val response: List<StudyEntity> = gson.fromJson(result, typeToken)
            val studyList = response.map { it.toStudy() }
            callback.onReturn(studyList)
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

    override fun removeStudy(study: Study) {
        executeJavascript(scriptManager.getRemoveStudyScript(study.name))
    }

    /**
     * Adds a selected type of study to active studies
     * @param study - a study to add/clone
     * @param forClone - if [study] is from  [getStudyList] use `false`,
     * if [study] is from [getActiveStudies] use `true`
     */
    override fun addStudy(study: Study, forClone: Boolean) {
        val key = if (forClone) {
            study.type!!
        } else {
            study.shortName
        }

        val scripts = scriptManager.getAddStudyScript(key)
        executeJavascript(scripts)
    }

    override fun setDrawingParameter(parameter: DrawingParameter, value: String) {
        executeJavascript(scriptManager.getSetDrawingParameterScript(parameter.value, value))
    }

    override fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>) {
        parameters = talkbackFields
    }

    override fun getStudyParameters(
        study: Study,
        type: StudyParameterType,
        callback: OnReturnCallback<String>
    ) {
        val script = when (type) {
            StudyParameterType.Inputs -> scriptManager.getStudyInputParametersScript(study.name)
            StudyParameterType.Outputs -> scriptManager.getStudyOutputParametersScript(study.name)
            StudyParameterType.Parameters -> scriptManager.getStudyParametersScript(study.name)
        }
        executeJavascript(script) { value ->
            callback.onReturn(value)
        }
    }

    private fun executeJavascript(script: String, callback: ValueCallback<String>? = null) {
        chartIQView.evaluateJavascript(script, callback)
    }

    private fun invokePullCallback(callbackId: String, data: List<OHLCParams>) {
        executeJavascript(scriptManager.getParseDataScript(data, callbackId))
    }

    companion object {
        private const val JAVASCRIPT_INTERFACE_QUOTE_FEED = "QuoteFeed"
        private const val JAVASCRIPT_INTERFACE_PARAMETERS = "parameters"
        private val TAG = ChartIQHandler.javaClass.simpleName
    }


}
