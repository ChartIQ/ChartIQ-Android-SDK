package com.chartiq.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.webkit.*
import com.chartiq.sdk.adapters.StudyEntityClassTypeAdapter
import com.chartiq.sdk.model.*
import com.chartiq.sdk.model.drawingtool.DrawingTool
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
    private val chartIQView = ChartIQView(context)

    override val chartView: View
        get() = chartIQView

    init {
        chartIQView.apply {
            with(settings) {
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
//                    executeJavascript(scriptManager.getNativeQuoteFeedScript())//todo comment until it is needed
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
     * Adds a selected study to active studies
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
    /**
     * Changes the active [Study] with a single parameter
     * @param study -  a [Study] to update
     * @param parameter - a [StudyParameterModel] that contains key-value to be updated
     */
    override fun setStudyParameter(study: Study, parameter: StudyParameterModel) {
        val script = scriptManager.getSetStudyParameterScript(study.name, parameter)
        executeJavascript(script)
    }

    /**
     * Changes the active [Study] with the provided inputs, outputs and parameters
     * @param study -  a [Study] to update
     * @param parameters -  a list of [StudyParameterModel] that contains values to be updated
     */
    override fun setStudyParameters(study: Study, parameters: List<StudyParameterModel>) {
        executeJavascript(scriptManager.getSetStudyParametersScript(study.name, parameters))
    }

    override fun setDrawingParameter(parameter: String, value: String) {
        executeJavascript(scriptManager.getSetDrawingParameterScript(parameter, value))
    }

    override fun getDrawingParameters(
        tool: DrawingTool,
        callback: OnReturnCallback<Map<String, Any>>
    ) {
        executeJavascript(scriptManager.getGetDrawingParametersScript(tool.value)) { value ->
            if (value != "null") {
                val result = value
                    .substring(1, value.length - 1)
                    .replace("\\", "")
                val typeToken = object : TypeToken<Map<String, Any>>() {}.type
                // TODO: 25.11.20 Fix NO_TOOL crashing
                val parameters: Map<String, Any> = Gson().fromJson(result, typeToken)
                callback.onReturn(parameters)
            }
        }
    }

    override fun deleteDrawing() {
        executeJavascript(scriptManager.getDeleteDrawingScript())
    }

    override fun cloneDrawing() {
        executeJavascript(scriptManager.getCloneDrawingScript())
    }

    override fun manageLayer(layer: ChartLayer) {
        executeJavascript(scriptManager.getLayerManagementScript(layer))
    }

    override fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>) {
        parameters = talkbackFields
    }

    override fun getStudyParameters(
        study: Study,
        type: StudyParameterType,
        callback: OnReturnCallback<List<StudyParameter>>
    ) {
        val script = when (type) {
            StudyParameterType.Inputs -> scriptManager.getStudyInputParametersScript(study.name)
            StudyParameterType.Outputs -> scriptManager.getStudyOutputParametersScript(study.name)
            StudyParameterType.Parameters -> scriptManager.getStudyParametersScript(study.name)
        }
        executeJavascript(script) { value ->
            val typeToken = object : TypeToken<List<StudyParameterEntity>>() {}.type
            val resultEntity = Gson().fromJson<List<StudyParameterEntity>>(value, typeToken)
            callback.onReturn(resultEntity.map { it.toParameter(type) })
        }
    }

    override fun getHUDDetails(callback: OnReturnCallback<CrosshairHUD>) {
        executeJavascript(scriptManager.getGetCrosshairHUDDetailsScript()) { value ->
            if (value != "null") {
                val hud = Gson().fromJson(value, CrosshairHUD::class.java)
                callback.onReturn(hud)
            }
        }
    }

    override fun undoDrawing(callback: OnReturnCallback<Boolean>) {
        executeJavascript(scriptManager.getUndoDrawingScript())
    }

    override fun redoDrawing(callback: OnReturnCallback<Boolean>) {
        executeJavascript(scriptManager.getRedoDrawingScript())
    }

    private fun executeJavascript(script: String, callback: ValueCallback<String>? = null) {
        Log.d(TAG, "Script executed: \n $script")
        chartIQView.evaluateJavascript(script, callback)
    }

    private fun invokePullCallback(callbackId: String, data: List<OHLCParams>) {
        executeJavascript(scriptManager.getParseDataScript(data, callbackId))
    }

    companion object {
        private const val JAVASCRIPT_INTERFACE_QUOTE_FEED = "QuoteFeed"
        private const val JAVASCRIPT_INTERFACE_PARAMETERS = "parameters"
        private val TAG = ChartIQHandler::class.java.simpleName
    }
}

