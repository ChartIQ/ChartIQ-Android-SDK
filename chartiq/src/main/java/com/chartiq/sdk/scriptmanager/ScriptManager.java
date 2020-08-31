package com.chartiq.sdk.scriptmanager;

import com.chartiq.sdk.model.AggregationType;
import com.chartiq.sdk.model.DrawingTool;
import com.chartiq.sdk.model.OHLCParams;

import org.json.JSONObject;

import java.util.Map;

public interface ScriptManager {

    String getDetermineOSScript();

    String getNativeQuoteFeedScript();

    String getAddDrawingListenerScript();

    String getAddLayoutListenerScript();

    String getSetSymbolScript(String symbol);

    // TODO: 26.08.20 Review the following method
    String getDateFromTickScript();

    String getSetDataMethodScript(String symbol);

    String getSetAccessibilityModeScript();

    String getIsChartAvailableScript();

    String getSetSymbolObjectScript(JSONObject symbolObject);

    String getSetPeriodicityScript(int period, String interval, String timeUnit);

    String getGetChartNameScript();

    String getPushDataScript(String symbol, OHLCParams[] data);

    String getPushUpdateScript(OHLCParams[] data);

    String getSetChartTypeScript(String chartType);

    String getAddComparisonScript(String symbol, String hexColor, Boolean isComparison);

    String getRemoveComparisonScript(String symbol);

    String getClearChartScript();

    String getSetChartScaleScript(String scale);

    String getAddStudyScript(String studyName, Map<String, Object> inputs, Map<String, Object> outputs, Map<String, Object> parameters);

    String getRemoveStudyScript(String studyName);

    String getRemoveAllStudiesScript();

    String getEnableCrosshairsScript();

    String getIsCrosshairsEnabledScript();

    String getDisableCrosshairsScript();

    String getGetCrosshairsHUDDetailScript();

    String getEnableDrawingScript(DrawingTool type);

    String getDisableDrawingScript();

    String getClearDrawingScript();

    String getSetDrawingParameterScript(String parameter, String value);

    String getSetStyleScript(String object, String parameter, String value);

    String getSetThemeScript(String theme);

    String getGetStudyListScript();

    String getGetActiveStudiesScript();

    String getSetAggregationTypeScript(AggregationType aggregationType);

    String getGetStudyInputParametersScript(String studyName);

    String getGetStudyOutputParametersScript(String studyName);

    String getGetStudyParametersScript(String studyName);

    String getSetStudyInputParameterScript(String studyName, String parameter, String value);

    String getSetStudyOutputParameterScript(String studyName, String parameter, String value);

    String getGetDrawingParametersScript(String drawingName);

    // TODO: 27.08.20 Review the following set of functions
    String getChangeChartStyleScript(final Object... args);

    String getSetChartPropertyScript(final String property, final Object value);

    String getGetChartPropertyScript(final String property);

    String getSetEnginePropertyScript(final String property, final Object value);

    String getGetEnginePropertyScript(final String property);

    String getParseDataScript(OHLCParams[] data, String callbackId);
}
