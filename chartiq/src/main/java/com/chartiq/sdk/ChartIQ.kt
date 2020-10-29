package com.chartiq.sdk

import com.chartiq.sdk.model.*
import com.chartiq.sdk.model.drawingtool.DrawingParameter
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.sdk.model.drawingtool.DrawingToolParameters
import java.util.*

interface ChartIQ {

    fun start(chartIQUrl: String, onStartCallback: OnStartCallback)

    fun setSymbol(symbol: String)

    fun setDataSource(dataSource: DataSource)

    fun setDataMethod(method: DataMethod, symbol: String)

    fun enableCrosshairs()

    fun disableCrosshairs()

    fun setPeriodicity(period: Int, interval: String, timeUnit: String)

    fun enableDrawing(type: DrawingTool)

    fun disableDrawing()

    fun clearDrawing()

    fun getStudyList(callback: OnReturnCallback<List<Study>>)

    fun getActiveStudies(callback: OnReturnCallback<List<Study>>)

    fun setAggregationType(aggregationType: AggregationType)

    fun setChartType(chartType: ChartType)

    fun setChartScale(scale: ChartScale)

    fun removeStudy(studyName: String)

    fun addStudy(study: Study, firstLoad: Boolean)

    fun setDrawingParameter(parameter: DrawingParameter, value: String)

    fun getDrawingParameters(tool: DrawingTool, callback: OnReturnCallback<DrawingToolParameters>)

    fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>)

    fun getStudyInputParameters(studyName: String, callback: OnReturnCallback<String>)

    fun getStudyOutputParameters(studyName: String, callback: OnReturnCallback<String>)

    fun getStudyParameters(studyName: String, callback: OnReturnCallback<String>)
}
