package com.chartiq.sdk;

import com.chartiq.sdk.model.Study;

import java.util.HashMap;
import java.util.Map;

public interface ChartIQWebView {

    void start(final String chartIQUrl, final CallbackStart callbackStart);

    void setSymbol(String symbol);

    void setDataSource(DataSource dataSource);

    void setDataMethod(DataMethod method, String symbol);

    void setShowDebugInfo(boolean showDebugInfo);

    void enableCrosshairs();

    void disableCrosshairs();

    void setPeriodicity(int period, String interval, String timeUnit);

    void enableDrawing(DrawingType type);

    void disableDrawing();

    void clearDrawing();

    Promise<Study[]> getStudyList();

    Promise<Study[]> getActiveStudies();

    void setAggregationType(AggregationType aggregationType);

    void setChartType(String chartType);

    void setChartScale(String scale);

    void removeStudy(String studyName);

    void addStudy(String studyName, Map<String, Object> inputs, Map<String, Object> outputs, Map<String, Object> parameters);
    void addStudy(Study study, boolean firstLoad);

    void setDrawingParameter(String parameter, String value);

    void setTalkbackFields(HashMap<String, Boolean> talkbackFields);

    Promise<String> getStudyInputParameters(String studyName);

    Promise<String> getStudyOutputParameters(String studyName);

    Promise<String> getStudyParameters(String studyName);
}
