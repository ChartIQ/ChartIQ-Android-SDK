package com.chartiq.sdk

import com.chartiq.sdk.model.*
import java.util.*

interface ChartIQ: ChartIQDrawingTool {

    fun start( onStartCallback: OnStartCallback)

    fun setSymbol(symbol: String)

    fun setDataSource(dataSource: DataSource)

    fun setDataMethod(method: DataMethod, symbol: String)

    fun enableCrosshairs()

    fun disableCrosshairs()

    fun setPeriodicity(period: Int, interval: String, timeUnit: String)

    fun getStudyList(callback: OnReturnCallback<List<Study>>)

    fun getActiveStudies(callback: OnReturnCallback<List<Study>>)

    fun setAggregationType(aggregationType: AggregationType)

    fun setChartType(chartType: ChartType)

    fun setChartScale(scale: ChartScale)

    fun removeStudy(studyName: String)

    fun addStudy(study: Study, firstLoad: Boolean)

    fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>)

    fun getStudyInputParameters(studyName: String, callback: OnReturnCallback<String>)

    fun getStudyOutputParameters(studyName: String, callback: OnReturnCallback<String>)

    fun getStudyParameters(studyName: String, callback: OnReturnCallback<String>)

    fun getHUDDetails(callback: OnReturnCallback<CrosshairHUD>)

    fun undo(callback: OnReturnCallback<Boolean>)

    fun redo(callback: OnReturnCallback<Boolean>)
}
