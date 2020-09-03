package com.chartiq.sdk.scriptmanager

import com.chartiq.sdk.model.AggregationType
import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.OHLCParams
import org.json.JSONObject

interface ScriptManager {

    fun getDetermineOSScript(): String

    fun getNativeQuoteFeedScript(): String

    fun getAddDrawingListenerScript(): String

    fun getAddLayoutListenerScript(): String

    fun getSetSymbolScript(symbol: String): String

    fun getDateFromTickScript(): String

    fun getSetDataMethodScript(symbol: String): String

    fun getSetAccessibilityModeScript(): String

    fun getIsChartAvailableScript(): String

    fun getSetSymbolObjectScript(symbolObject: JSONObject): String

    fun getSetPeriodicityScript(period: Int, interval: String, timeUnit: String): String

    fun getGetChartNameScript(): String

    fun getPushDataScript(symbol: String, data: Array<OHLCParams>): String

    fun getPushUpdateScript(data: Array<OHLCParams>): String

    fun getSetChartTypeScript(chartType: String): String

    fun getAddComparisonScript(symbol: String, hexColor: String, isComparison: Boolean): String

    fun getRemoveComparisonScript(symbol: String): String

    fun getClearChartScript(): String

    fun getSetChartScaleScript(scale: String): String

    fun getAddStudyScript(
        studyName: String,
        inputs: Map<String, Any>?,
        outputs: Map<String, Any>?,
        parameters: Map<String, Any>
    ): String

    fun getRemoveStudyScript(studyName: String): String

    fun getRemoveAllStudiesScript(): String

    fun getEnableCrosshairsScript(): String

    fun getIsCrosshairsEnabledScript(): String

    fun getDisableCrosshairsScript(): String

    fun getGetCrosshairsHUDDetailScript(): String

    fun getEnableDrawingScript(type: DrawingTool): String

    fun getDisableDrawingScript(): String

    fun getClearDrawingScript(): String

    fun getSetDrawingParameterScript(parameter: String, value: String): String

    fun getSetStyleScript(obj: String, parameter: String, value: String): String

    fun getSetThemeScript(theme: String): String

    fun getGetStudyListScript(): String

    fun getGetActiveStudiesScript(): String

    fun getSetAggregationTypeScript(aggregationType: AggregationType): String

    fun getGetStudyInputParametersScript(studyName: String): String

    fun getGetStudyOutputParametersScript(studyName: String): String

    fun getGetStudyParametersScript(studyName: String): String

    fun getSetStudyInputParameterScript(studyName: String, parameter: String, value: String): String

    fun getSetStudyOutputParameterScript(
        studyName: String,
        parameter: String,
        value: String
    ): String

    fun getGetDrawingParametersScript(drawingName: String): String

    fun getChangeChartStyleScript(vararg args: Any): String

    fun getSetChartPropertyScript(property: String, value: Any): String

    fun getGetChartPropertyScript(property: String): String

    fun getSetEnginePropertyScript(property: String, value: Any): String

    fun getGetEnginePropertyScript(property: String): String

    fun getParseDataScript(data: Array<OHLCParams>, callbackId: String): String
}
