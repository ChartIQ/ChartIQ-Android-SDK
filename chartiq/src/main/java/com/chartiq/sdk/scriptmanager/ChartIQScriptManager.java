package com.chartiq.sdk.scriptmanager;

import com.chartiq.sdk.model.AggregationType;
import com.chartiq.sdk.model.DrawingTool;
import com.chartiq.sdk.model.OHLCParams;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;

import static com.chartiq.sdk.Util.buildArgumentStringFromArgs;

public class ChartIQScriptManager implements ScriptManager {

    private String mobileNameSpace = "CIQ.MobileBridge.";
    private static final String CHART_IQ_JS_OBJECT = "stxx.";

    @Override
    public String getDetermineOSScript() {
        String script = mobileNameSpace + "determineOs()";
        return script;
    }

    @Override
    public String getNativeQuoteFeedScript() {
        String script = mobileNameSpace + "nativeQuoteFeed(parameters, cb)";
        return script;
    }

    @Override
    public String getAddDrawingListenerScript() {
        String script = mobileNameSpace + "addDrawingListener()";
        return script;
    }

    @Override
    public String getAddLayoutListenerScript() {
        String script = mobileNameSpace + "addLayoutListener()";
        return script;
    }

    @Override
    public String getSetDataMethodScript(String symbol) {
        String functionName = "newChart";
        String script = CHART_IQ_JS_OBJECT + functionName + "(" + symbol + ")";
        return script;
    }

    @Override
    public String getSetSymbolScript(String symbol) {
        String script = mobileNameSpace + "loadChart" + "(" + "\"" + symbol + "\"" + ")";
        return script;
    }

    @Override
    public String getDateFromTickScript() {
        String functionName = "dateFromTick";
        String script = CHART_IQ_JS_OBJECT + functionName + "(" + 1 + ")";
        return script;
    }

    @Override
    public String getSetAccessibilityModeScript() {
        String script = mobileNameSpace + "accessibilityMode()";
        return script;
    }

    @Override
    public String getIsChartAvailableScript() {
        return null;
    }

    @Override
    public String getSetSymbolObjectScript(JSONObject symbolObject) {
        return null;
    }

    @Override
    public String getSetPeriodicityScript(int period, String interval, String timeUnit) {
        if (timeUnit == null) {
            timeUnit = "minute";
        }
        String args = buildArgumentStringFromArgs(period, interval, timeUnit);
        String script = mobileNameSpace + "setPeriodicity(" + period + ", \"" + interval + "\", \"" + timeUnit + "\");";
        return script;
    }

    @Override
    public String getGetChartNameScript() {
        return null;
    }

    @Override
    public String getPushDataScript(String symbol, OHLCParams[] data) {
        return null;
    }

    @Override
    public String getPushUpdateScript(OHLCParams[] data) {
        return null;
    }

    @Override
    public String getSetChartTypeScript(String chartType) {
        String functionName = "setChartType";
        String script = CHART_IQ_JS_OBJECT + functionName + "(" + buildArgumentStringFromArgs(chartType) + ")";
        return script;
    }

    @Override
    public String getAddComparisonScript(String symbol, String hexColor, Boolean isComparison) {
        return null;
    }

    @Override
    public String getRemoveComparisonScript(String symbol) {
        return null;
    }

    @Override
    public String getClearChartScript() {
        return null;
    }

    @Override
    public String getSetChartScaleScript(String scale) {
        String functionName = "setChartScale";
        String script = CHART_IQ_JS_OBJECT + functionName + "(" + scale + ")";
        return script;
    }

    @Override
    public String getAddStudyScript(String studyName, Map<String, Object> inputs, Map<String, Object> outputs, Map<String, Object> parameters) {
        String functionName = "addStudy";
        String script = functionName + "(" + buildArgumentStringFromArgs(studyName, inputs, outputs, parameters) + ")";
        return script;
    }

    @Override
    public String getRemoveStudyScript(String studyName) {
        String script = mobileNameSpace + "removeStudy" + "(" + "\"" + studyName + "\"" + ")";
        return script;
    }

    @Override
    public String getRemoveAllStudiesScript() {
        return null;
    }

    @Override
    public String getEnableCrosshairsScript() {
        String script = mobileNameSpace + "enableCrosshairs(true);";
        return script;
    }

    @Override
    public String getIsCrosshairsEnabledScript() {
        return null;
    }

    @Override
    public String getDisableCrosshairsScript() {
        String script = mobileNameSpace + "enableCrosshairs(false);";
        return script;
    }

    @Override
    public String getGetCrosshairsHUDDetailScript() {
        return null;
    }

    @Override
    public String getEnableDrawingScript(DrawingTool type) {
        String functionName = "changeVectorType";
        String script = CHART_IQ_JS_OBJECT + functionName + "(" + buildArgumentStringFromArgs(type.getValue()) + ")";
        return script;
    }

    @Override
    public String getDisableDrawingScript() {
        String script = getEnableDrawingScript(DrawingTool.NONE);
        return script;
    }

    @Override
    public String getClearDrawingScript() {
        String functionName = "clearDrawings";
        String script = CHART_IQ_JS_OBJECT + functionName + "(" + ")";
        return script;
    }

    @Override
    public String getSetDrawingParameterScript(String parameter, String value) {
        String script = mobileNameSpace + "setCurrentVectorParameters" + '(' + parameter + ", " + value + ')' + ';';
        return script;
    }

    @Override
    public String getSetStyleScript(String object, String parameter, String value) {
        return null;
    }

    @Override
    public String getSetThemeScript(String theme) {
        return null;
    }

    @Override
    public String getGetStudyListScript() {
        String script = mobileNameSpace + "getStudyList();";
        return script;
    }

    @Override
    public String getGetActiveStudiesScript() {
        String script = mobileNameSpace + "getActiveStudies();";
        return script;
    }

    @Override
    public String getSetAggregationTypeScript(AggregationType aggregationType) {
        String functionName = "setAggregationType";
        String value = aggregationType.getValue();
        String script = CHART_IQ_JS_OBJECT + functionName + "(" + value + ")";
        return script;
    }

    @Override
    public String getGetStudyInputParametersScript(String studyName) {
        String script = mobileNameSpace + "getStudyParameters(\"" + studyName + "\" , \"inputs\");";
        return script;
    }

    @Override
    public String getGetStudyOutputParametersScript(String studyName) {
        String script = mobileNameSpace + "getStudyParameters(\"" + studyName + "\" , \"outputs\");";
        return script;
    }

    @Override
    public String getGetStudyParametersScript(String studyName) {
        String script = mobileNameSpace + "getStudyParameters(\"" + studyName + "\" , \"parameters\");";
        return script;
    }

    @Override
    public String getSetStudyInputParameterScript(String studyName, String parameter, String value) {
        return null;
    }

    @Override
    public String getSetStudyOutputParameterScript(String studyName, String parameter, String value) {
        return null;
    }

    @Override
    public String getGetDrawingParametersScript(String drawingName) {
        return null;
    }

    @Override
    public String getChangeChartStyleScript(Object... args) {
        return null;
    }

    @Override
    public String getSetChartPropertyScript(String property, Object value) {
        return null;
    }

    @Override
    public String getGetChartPropertyScript(String property) {
        return null;
    }

    @Override
    public String getSetEnginePropertyScript(String property, Object value) {
        return null;
    }

    @Override
    public String getGetEnginePropertyScript(String property) {
        return null;
    }


    @Override
    public String getParseDataScript(OHLCParams[] data, String callbackId) {
        String json = new Gson().toJson(data);
        String script = mobileNameSpace + "parseData('" + json + "', \"" + callbackId + "\");";
        return script;
    }
}
