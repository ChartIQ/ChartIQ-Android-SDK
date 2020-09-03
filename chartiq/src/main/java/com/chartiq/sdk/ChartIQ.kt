package com.chartiq.sdk

import com.chartiq.sdk.model.AggregationType
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.Study
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

    fun getStudyList(callback: OnReturnCallback<Array<Study>>)

    fun getActiveStudies(callback: OnReturnCallback<Array<Study>>)

    fun setAggregationType(aggregationType: AggregationType)

    fun setChartType(chartType: String)

    fun setChartScale(scale: String)

    fun removeStudy(studyName: String)

    fun addStudy(study: Study, firstLoad: Boolean)

    fun setDrawingParameter(parameter: String, value: String)

    fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>)

    fun getStudyInputParameters(studyName: String, callback: OnReturnCallback<String>)

    fun getStudyOutputParameters(studyName: String, callback: OnReturnCallback<String>)

    fun getStudyParameters(studyName: String, callback: OnReturnCallback<String>)
}
