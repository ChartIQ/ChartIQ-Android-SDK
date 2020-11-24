package com.chartiq.sdk.scriptmanager

import com.chartiq.sdk.buildArgumentStringFromArgs
import com.chartiq.sdk.model.AggregationType
import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.OHLCParams
import com.chartiq.sdk.model.StudyParameterModel
import com.google.gson.Gson

// TODO: 03.09.20 Add parameters safety check
internal class ChartIQScriptManager : ScriptManager {

    override fun getDetermineOSScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "determineOs()"

    override fun getNativeQuoteFeedScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "nativeQuoteFeed(parameters, cb)"

    override fun getAddDrawingListenerScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "addDrawingListener();"

    override fun getAddLayoutListenerScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "addLayoutListener()"

    override fun getAddMeasureListener(): String =
        MOBILE_BRIDGE_NAME_SPACE + "addMeasureListener();"

    override fun getSetSymbolScript(symbol: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "loadChart(\"$symbol\")"

    override fun getDateFromTickScript(): String =
        CHART_IQ_JS_OBJECT + "dateFromTick(${1})"

    override fun getSetDataMethodScript(symbol: String): String =
        CHART_IQ_JS_OBJECT + "loadChart(\"$symbol\");"

    override fun getSetAccessibilityModeScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "accessibilityMode();"

    override fun getIsChartAvailableScript(): String =
        "if (" + MOBILE_BRIDGE_NAME_SPACE + "isChartAvailable() == true) { \"true\" } else { \"false\" } "

    override fun getSetPeriodicityScript(period: Int, interval: String, timeUnit: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "setPeriodicity($period, $interval, \"$timeUnit\");"

    override fun getPushDataScript(symbol: String, data: List<OHLCParams>): String {
        // TODO: 03.09.20 Parse the array for the call
        val jsonString = ""
        return MOBILE_BRIDGE_NAME_SPACE + "loadChart(\"\", $jsonString); "
    }

    override fun getPushUpdateScript(data: List<OHLCParams>): String {
        // TODO: 03.09.20 Parse the array for the call
        return MOBILE_BRIDGE_NAME_SPACE + "parseData($data');"
    }

    override fun getSetChartTypeScript(chartType: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "setChartType(\"$chartType\");"

    override fun getAddSeriesScript(symbol: String, hexColor: String): String =
        CHART_IQ_JS_OBJECT + "addSeries(\"$symbol\", {display:\"$symbol\", " +
                "color: \"$hexColor\"  isComparison:true});"

    override fun getRemoveSeriesScript(symbol: String): String =
        CHART_IQ_JS_OBJECT + "removeSeries(\"$symbol\");"

    override fun getResizeChartScript(): String =
        CHART_IQ_JS_OBJECT + "resizeChart();"

    override fun getClearChartScript(): String =
        CHART_IQ_JS_OBJECT + "destroy();"

    override fun getSetChartScaleScript(scale: String): String = CHART_IQ_JS_OBJECT + "layout.chartScale = \"$scale\";"

    override fun getAddStudyScript(studyName: String): String = MOBILE_BRIDGE_NAME_SPACE + "addStudy(\"$studyName\");"

    override fun getRemoveStudyScript(studyName: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "removeStudy(\"$studyName\");"

    override fun getRemoveAllStudiesScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "removeAllStudies();"

    override fun getEnableCrosshairScript(value: Boolean): String =
        MOBILE_BRIDGE_NAME_SPACE + "enableCrosshairs($value);"

    override fun getIsCrosshairsEnabledScript(): String =
        "if (${CHART_IQ_JS_OBJECT}layout.crosshair == true) { \"true\" } else { \"false\" } "

    override fun getGetCrosshairsHUDDetailScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "getHudDetails();"

    override fun getEnableDrawingScript(type: DrawingTool): String =
        CHART_IQ_JS_OBJECT + "changeVectorType(" + buildArgumentStringFromArgs(type.value) + ");"

    override fun getDisableDrawingScript(): String = getEnableDrawingScript(DrawingTool.NO_TOOL)

    override fun getClearDrawingScript(): String = CHART_IQ_JS_OBJECT + "clearDrawings();"

    // TODO: 03.09.20 Look into alternative "setDrawingParameters()"
    override fun getSetDrawingParameterScript(parameter: String, value: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "setCurrentVectorParameters($parameter, $value)"

    override fun getSetStyleScript(obj: String, parameter: String, value: String): String =
        CHART_IQ_JS_OBJECT + "setStyle(\"$obj\", \"$parameter\", \"$value\");"

    override fun getSetThemeScript(theme: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "setTheme(\"$theme\");"

    override fun getGetStudyListScript(): String = "JSON.stringify(CIQ.Studies.studyLibrary);"

    override fun getGetActiveStudiesScript(): String =
        MOBILE_BRIDGE_NAME_SPACE + "getActiveStudies();"

    override fun getSetAggregationTypeScript(aggregationType: AggregationType): String =
        CHART_IQ_JS_OBJECT + "setAggregationType" + "(" + aggregationType.value + ");"

    override fun getStudyInputParametersScript(studyName: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "getStudyParameters(\"$studyName\", \"inputs\")"

    override fun getStudyOutputParametersScript(studyName: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "getStudyParameters(\"$studyName\" , \"outputs\")"

    override fun getStudyParametersScript(studyName: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "getStudyParameters(\"$studyName\" , \"parameters\")"

    override fun getSetStudyParameterScript(studyName: String, parameter: StudyParameterModel): String {
        val script =
            MOBILE_BRIDGE_NAME_SPACE + "setStudy(\"$studyName\", \"${parameter.fieldName.asSafeScriptParameter}\", \"${parameter.fieldSelectedValue.asSafeScriptParameter}\");"
        return script
    }

    override fun getSetStudyParametersScript(name: String, parameters: List<StudyParameterModel>): String {
        val scriptList = parameters.map {
            getUpdateStudyParametersScript(it.fieldName, it.fieldSelectedValue)
        }
        return getStudyDescriptorScript(name) +
                "var helper = new CIQ.Studies.DialogHelper({\n" +
                "\tsd: selectedSd,\n" +
                "\tstx: stxx\n" +
                "});\n" +
                "var isFound = false;\n" +
                "var newInputParameters = {};\n" +
                "var newOutputParameters = {};\n" +
                "var newParameters = {};" +
                scriptList.joinToString(" ") +
                "helper.updateStudy({\n" +
                "\tinputs: newInputParameters,\n" +
                "\toutputs: newOutputParameters,\n" +
                "\tparameters: newParameters\n" +
                "});\n" +
                "console.log(JSON.stringify(newInputParameters));\n" +
                "console.log(JSON.stringify(newOutputParameters));\n" +
                "console.log(JSON.stringify(newParameters));"
    }

    private fun getStudyDescriptorScript(name: String): String {
        val safeStudyName = name.asSafeScriptParameter
        return "var s = stxx.layout.studies;\n" +
                "var selectedSd = {};\n" +
                "for (var n in s) {\n" +
                "\tvar sd = s[n];\n" +
                "\tif (sd.name === \"$safeStudyName\") {\n" +
                "\t\tselectedSd = sd;\n" +
                "\t}\n" +
                "}"

    }

    private fun getUpdateStudyParametersScript(key: String, value: String): String {
        val safeStudyParameter = key.asSafeScriptParameter
        val safeStudyValue = value.asSafeScriptParameter
        val script = "for (x in helper.inputs) {" +
                "   var input = helper.inputs[x]; " +
                "   if (input[\"name\"] === \"$safeStudyParameter\") { " +
                "       isFound = true; " +
                "       if (input[\"type\"] === \"text\" || input[\"type\"] === \"select\") { " +
                "           newInputParameters[\"$safeStudyParameter\"] = \"$safeStudyValue\"; " +
                "       } else if (input[\"type\"] === \"number\") { " +
                "           newInputParameters[\"$safeStudyParameter\"] = parseFloat(\"$safeStudyValue\"); " +
                "       } else if (input[\"type\"] === \"checkbox\") { " +
                "           newInputParameters[\"$safeStudyParameter\"] =" +
                " (\"$safeStudyValue\" == \"false\" || \"$safeStudyValue\" == \"0\" ? false : true); " +
                "       } " +
                "   } " +
                "} " +
                "if (isFound == false) { " +
                "   for (x in helper.outputs) { " +
                "       var output = helper.outputs[x]; " +
                "       if (output[\"name\"] === \"$safeStudyParameter\") { " +
                "           newOutputParameters[\"$safeStudyParameter\"] = \"$safeStudyValue\"; " +
                "       } " +
                "   } " +
                "} " +
                "if (isFound == false) { " +
                "   if(\"$safeStudyParameter\".includes(\"Color\")) { " +
                "       newParameters[\"$safeStudyParameter\"] = \"$safeStudyValue\"; " +
                "   } else if(\"$safeStudyParameter\".includes(\"Enabled\")) { " +
                "       newParameters[\"$safeStudyParameter\"] =" +
                " (\"$safeStudyValue\" == \"false\" || \"$safeStudyValue\" == \"0\" ? false : true); " +
                "   } else { " +
                "       newParameters[\"$safeStudyParameter\"] = parseFloat(\"$safeStudyValue\"); " +
                "   } " +
                "} " + "isFound = false; "
        return script
    }


    override fun getGetDrawingParametersScript(drawingName: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "getDrawingParameters(\"$drawingName\");"

    override fun getSetChartStyleScript(obj: String, attribute: String, value: String): String =
        CHART_IQ_JS_OBJECT + "setStyle(\"$obj\", \"$attribute\", \"$value\")"

    override fun getSetChartPropertyScript(property: String, value: Any): String {
        TODO("Not yet implemented")
    }

    override fun getGetChartPropertyScript(property: String): String {
        TODO("Not yet implemented")
    }

    override fun getSetEnginePropertyScript(property: String, value: Any): String {
        TODO("Not yet implemented")
    }

    override fun getGetEnginePropertyScript(property: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "getEngineProperty(\"$property\");"

    override fun getParseDataScript(data: List<OHLCParams>, callbackId: String): String =
        MOBILE_BRIDGE_NAME_SPACE + "parseData('${Gson().toJson(data)}', \"$callbackId\")"

    companion object {
        private const val MOBILE_BRIDGE_NAME_SPACE = "CIQ.MobileBridge."
        private const val CHART_IQ_JS_OBJECT = "stxx."
    }

    private val String.asSafeScriptParameter: String
        get() {
            //todo check how it works
            return this.replace("(?i)(<.?\\s+)on.?(>.*?)", "$1$2")
        }
}
