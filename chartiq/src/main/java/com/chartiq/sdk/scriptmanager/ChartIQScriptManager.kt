package com.chartiq.sdk.scriptmanager

import com.chartiq.sdk.buildArgumentStringFromArgs
import com.chartiq.sdk.model.AggregationType
import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.OHLCParams
import com.google.gson.Gson
import org.json.JSONObject

class ChartIQScriptManager : ScriptManager {

    override fun getDetermineOSScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "determineOs()"

    override fun getNativeQuoteFeedScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "nativeQuoteFeed(parameters, cb)"

    override fun getAddDrawingListenerScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "addDrawingListener()"

    override fun getAddLayoutListenerScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "addLayoutListener()"

    override fun getSetSymbolScript(symbol: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "loadChart(\"$symbol\")"

    override fun getDateFromTickScript(): String =
        CHART_IQ_JS_OBJECT + "dateFromTick(${1})"

    override fun getSetDataMethodScript(symbol: String): String =
        CHART_IQ_JS_OBJECT + "newChart($symbol)"

    override fun getSetAccessibilityModeScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "accessibilityMode()"

    override fun getIsChartAvailableScript(): String {
        TODO("Not yet implemented")
    }

    override fun getSetSymbolObjectScript(symbolObject: JSONObject): String {
        TODO("Not yet implemented")
    }

    override fun getSetPeriodicityScript(period: Int, interval: String, timeUnit: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "setPeriodicity($period, $interval, \"$timeUnit\")"

    override fun getGetChartNameScript(): String {
        TODO("Not yet implemented")
    }

    override fun getPushDataScript(symbol: String, data: Array<OHLCParams>): String {
        TODO("Not yet implemented")
    }

    override fun getPushUpdateScript(data: Array<OHLCParams>): String {
        TODO("Not yet implemented")
    }

    override fun getSetChartTypeScript(chartType: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "setChartType(\"$chartType\")"

    override fun getAddComparisonScript(
        symbol: String,
        hexColor: String,
        isComparison: Boolean
    ): String {
        TODO("Not yet implemented")
    }

    override fun getRemoveComparisonScript(symbol: String): String {
        TODO("Not yet implemented")
    }

    override fun getClearChartScript(): String {
        TODO("Not yet implemented")
    }

    override fun getSetChartScaleScript(scale: String): String =
        CHART_IQ_JS_OBJECT + "setChartScale($scale)"

    override fun getAddStudyScript(
        studyName: String,
        inputs: Map<String, Any>?,
        outputs: Map<String, Any>?,
        parameters: Map<String, Any>
    ): String = MOBILE_BRIDGE_NAME_SPACE + "addStudy(" + buildArgumentStringFromArgs(
        studyName,
        inputs,
        outputs,
        parameters
    ) + ")"

    override fun getRemoveStudyScript(studyName: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "removeStudy" + "(\"$studyName\")"

    override fun getRemoveAllStudiesScript(): String {
        TODO("Not yet implemented")
    }

    override fun getEnableCrosshairsScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "enableCrosshairs(${true}})"

    override fun getIsCrosshairsEnabledScript(): String {
        TODO("Not yet implemented")
    }

    override fun getDisableCrosshairsScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "enableCrosshairs(${false})"

    override fun getGetCrosshairsHUDDetailScript(): String {
        TODO("Not yet implemented")
    }

    override fun getEnableDrawingScript(type: DrawingTool): String =
        CHART_IQ_JS_OBJECT + "changeVectorType(" + buildArgumentStringFromArgs(type.value) + ")"

    override fun getDisableDrawingScript(): String = getEnableDrawingScript(DrawingTool.NONE)

    override fun getClearDrawingScript(): String = CHART_IQ_JS_OBJECT + "clearDrawings()"

    override fun getSetDrawingParameterScript(parameter: String, value: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "setCurrentVectorParameters($parameter, $value)"

    override fun getSetStyleScript(obj: String, parameter: String, value: String): String {
        TODO("Not yet implemented")
    }

    override fun getSetThemeScript(theme: String): String {
        TODO("Not yet implemented")
    }

    override fun getGetStudyListScript(): String = MOBILE_BRIDGE_NAME_SPACE + "getStudyList()"

    override fun getGetActiveStudiesScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "getActiveStudies()"

    override fun getSetAggregationTypeScript(aggregationType: AggregationType): String =
        CHART_IQ_JS_OBJECT + "setAggregationType" + "(" + aggregationType.value + ")"

    override fun getGetStudyInputParametersScript(studyName: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "getStudyParameters(\"$studyName\", \"inputs\")"

    override fun getGetStudyOutputParametersScript(studyName: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "getStudyParameters(\"$studyName\" , \"outputs\")"

    override fun getGetStudyParametersScript(studyName: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "getStudyParameters(\"$studyName\" , \"parameters\")"

    override fun getSetStudyInputParameterScript(
        studyName: String,
        parameter: String,
        value: String
    ): String {
        TODO("Not yet implemented")
    }

    override fun getSetStudyOutputParameterScript(
        studyName: String,
        parameter: String,
        value: String
    ): String {
        TODO("Not yet implemented")
    }

    override fun getGetDrawingParametersScript(drawingName: String): String {
        TODO("Not yet implemented")
    }

    override fun getChangeChartStyleScript(vararg args: Any): String {
        TODO("Not yet implemented")
    }

    override fun getSetChartPropertyScript(property: String, value: Any): String {
        TODO("Not yet implemented")
    }

    override fun getGetChartPropertyScript(property: String): String {
        TODO("Not yet implemented")
    }

    override fun getSetEnginePropertyScript(property: String, value: Any): String {
        TODO("Not yet implemented")
    }

    override fun getGetEnginePropertyScript(property: String): String {
        TODO("Not yet implemented")
    }

    override fun getParseDataScript(data: Array<OHLCParams>, callbackId: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "parseData('${Gson().toJson(data)}', \"$callbackId\")"

    companion object {
        private const val MOBILE_BRIDGE_NAME_SPACE = "CIQ.MobileBridge."
        private const val CHART_IQ_JS_OBJECT = "stxx."
    }
}
