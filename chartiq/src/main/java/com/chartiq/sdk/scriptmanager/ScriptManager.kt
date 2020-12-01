package com.chartiq.sdk.scriptmanager

import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.OHLCParams
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.study.StudyParameterModel

internal interface ScriptManager {

    fun getDetermineOSScript(): String

    fun getNativeQuoteFeedScript(): String

    fun getAddDrawingListenerScript(): String

    fun getAddLayoutListenerScript(): String

    fun getAddMeasureListener(): String

    fun getSetSymbolScript(symbol: String): String

    fun getDateFromTickScript(): String

    fun getSetDataMethodScript(symbol: String): String

    fun getSetAccessibilityModeScript(): String

    fun getIsChartAvailableScript(): String

    fun getSetPeriodicityScript(period: Int, interval: String, timeUnit: String): String

    fun getPushDataScript(symbol: String, data: List<OHLCParams>): String

    fun getPushUpdateScript(data: List<OHLCParams>): String

    fun getSetChartTypeScript(chartType: String): String

    fun getChartTypeScript(): String

    fun getAggregationTypeScript(): String

    fun getAddSeriesScript(symbol: String, hexColor: String): String

    fun getRemoveSeriesScript(symbol: String): String

    fun getResizeChartScript(): String

    fun getClearChartScript(): String

    fun getChartScaleScript(): String

    fun getSetChartScaleScript(scale: String): String

    fun getAddStudyScript(studyName: String): String

    fun getRemoveStudyScript(studyName: String): String

    fun getRemoveAllStudiesScript(): String

    fun getEnableCrosshairScript(value: Boolean): String

    fun getIsCrosshairsEnabledScript(): String

    fun getGetCrosshairsHUDDetailScript(): String

    fun getEnableDrawingScript(type: DrawingTool): String

    fun getDisableDrawingScript(): String

    fun getClearDrawingScript(): String

    fun getSetDrawingParameterScript(parameter: String, value: String): String

    fun getSetStyleScript(obj: String, parameter: String, value: String): String

    fun getSetThemeScript(theme: String): String

    fun getGetStudyListScript(): String

    fun getGetActiveStudiesScript(): String

    fun getSetAggregationTypeScript(aggregationType: AggregationChartType): String

    fun getStudyInputParametersScript(studyName: String): String

    fun getStudyOutputParametersScript(studyName: String): String

    fun getStudyParametersScript(studyName: String): String

    fun getSetStudyParametersScript(name: String, parameters: List<StudyParameterModel>): String

    fun getSetStudyParameterScript(studyName: String, parameter: StudyParameterModel): String

    fun getGetDrawingParametersScript(drawingName: String): String

    fun getSetChartStyleScript(obj: String, attribute: String, value: String): String

    fun getSetChartPropertyScript(property: String, value: Any): String

    fun getGetChartPropertyScript(property: String): String

    fun getSetEnginePropertyScript(property: String, value: Any): String

    fun getGetEnginePropertyScript(property: String): String

    fun getParseDataScript(data: List<OHLCParams>, callbackId: String): String

    fun getInvertYAxisScript(): String

    fun getSetInvertYAxisScript(inverted: Boolean): String

    fun getIsExtendedHoursScript(): String

    fun getSetExtendedHoursScript(extended: Boolean): String
}
