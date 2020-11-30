package com.chartiq.sdk

import com.chartiq.sdk.model.*
import java.util.*

interface ChartIQ: ChartIQStudy {

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

    override fun getStudyList(callback: OnReturnCallback<List<Study>>)

    override fun getActiveStudies(callback: OnReturnCallback<List<Study>>)

    fun setAggregationType(aggregationType: AggregationType)

    fun setChartType(chartType: ChartType)

    fun setChartScale(scale: ChartScale)

    override fun removeStudy(study: Study)

    override fun addStudy(study: Study, forClone: Boolean)

    override fun setStudyParameter(study: Study, parameter: StudyParameterModel)

    override fun setStudyParameters(study: Study, parameters: List<StudyParameterModel>)

    fun setDrawingParameter(parameter: DrawingParameter, value: String)

    fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>)

    override fun getStudyParameters(
        study: Study,
        type: StudyParameterType,
        callback: OnReturnCallback<List<StudyParameter>>
    )
}
