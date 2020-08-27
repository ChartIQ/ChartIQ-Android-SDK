package com.chartiq.sdk;

import com.chartiq.sdk.model.AggregationType;
import com.chartiq.sdk.model.DataMethod;
import com.chartiq.sdk.model.DrawingTool;
import com.chartiq.sdk.model.Study;

import java.util.HashMap;
import java.util.Map;

public interface ChartIQ {

    void start(final String chartIQUrl, final OnStartCallback onStartCallback);

    void setSymbol(String symbol);

    void setDataSource(DataSource dataSource);

    void setDataMethod(DataMethod method, String symbol);

    void setShowDebugInfo(boolean showDebugInfo);

    void enableCrosshairs();

    void disableCrosshairs();

    void setPeriodicity(int period, String interval, String timeUnit);

    void enableDrawing(DrawingTool type);

    void disableDrawing();

    void clearDrawing();

    Promise<Study[]> getStudyList();

    Promise<Study[]> getActiveStudies();

    void setAggregationType(AggregationType aggregationType);

    void setChartType(String chartType);

    void setChartScale(String scale);

    void removeStudy(String studyName);

    void addStudy(Study study, boolean firstLoad);

    void setDrawingParameter(String parameter, String value);

    void setTalkbackFields(HashMap<String, Boolean> talkbackFields);

    Promise<String> getStudyInputParameters(String studyName);

    Promise<String> getStudyOutputParameters(String studyName);

    Promise<String> getStudyParameters(String studyName);
}
