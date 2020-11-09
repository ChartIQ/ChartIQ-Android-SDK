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

    fun removeStudy(study: Study)

    fun addStudy(study: Study, forClone: Boolean)

    fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>)

    fun getHUDDetails(callback: OnReturnCallback<CrosshairHUD>)

    fun getStudyParameters(
        study: Study,
        type: StudyParameterType,
        callback: OnReturnCallback<String>
    )

    fun undo(callback: OnReturnCallback<Boolean>)

    fun redo(callback: OnReturnCallback<Boolean>)
}
