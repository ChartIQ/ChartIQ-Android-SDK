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

    void enableCrosshairs();

    void disableCrosshairs();

    void setPeriodicity(int period, String interval, String timeUnit);

    void enableDrawing(DrawingTool type);

    void disableDrawing();

    void clearDrawing();

    void getStudyList(final OnReturnCallback<Study[]> callback);

    void getActiveStudies(final OnReturnCallback<Study[]> callback);

    void setAggregationType(AggregationType aggregationType);

    void setChartType(String chartType);

    void setChartScale(String scale);

    void removeStudy(String studyName);

    void addStudy(Study study, boolean firstLoad);

    void setDrawingParameter(String parameter, String value);

    void setTalkbackFields(HashMap<String, Boolean> talkbackFields);

    void getStudyInputParameters(String studyName, final OnReturnCallback<String> callback);

    void getStudyOutputParameters(String studyName, final OnReturnCallback<String> callback);

    void getStudyParameters(String studyName, final OnReturnCallback<String> callback);
}
