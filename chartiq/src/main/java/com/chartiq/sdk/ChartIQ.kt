package com.chartiq.sdk

import com.chartiq.sdk.model.*
import java.util.*

interface ChartIQ {

    fun start(onStartCallback: OnStartCallback)

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

    fun removeStudy(study: Study)

    fun addStudy(study: Study, forClone: Boolean)

    fun setStudyParameter(study: Study, parameter: StudyParameterKeyValue)

    fun setStudyParameters(study: Study, parameters: List<StudyParameterKeyValue>)

    fun setDrawingParameter(parameter: DrawingParameter, value: String)

    fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>)

    fun getStudyParameters(
        study: Study,
        type: StudyParameterType,
        callback: OnReturnCallback<List<StudyParameter>>
    )
}
