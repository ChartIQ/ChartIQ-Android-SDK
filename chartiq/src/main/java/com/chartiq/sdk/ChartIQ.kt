package com.chartiq.sdk

import com.chartiq.sdk.model.*
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.charttype.ChartType
import com.chartiq.sdk.model.study.Study
import com.chartiq.sdk.model.study.StudyParameter
import com.chartiq.sdk.model.study.StudyParameterModel
import com.chartiq.sdk.model.study.StudyParameterType
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

    fun setAggregationType(aggregationType: AggregationChartType)

    fun setChartType(chartType: ChartType)

    fun getChartType(callback: OnReturnCallback<ChartType>)

    fun getAggregationChartType(callback: OnReturnCallback<AggregationChartType?>)

    fun getChartScale(callback: OnReturnCallback<ChartIQScale>)

    fun setChartScale(scale: ChartIQScale)

    fun removeStudy(study: Study)

    fun addStudy(study: Study, forClone: Boolean)

    fun setStudyParameter(study: Study, parameter: StudyParameterModel)

    fun setStudyParameters(study: Study, parameters: List<StudyParameterModel>)

    fun setDrawingParameter(parameter: DrawingParameter, value: String)

    fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>)

    fun getStudyParameters(
        study: Study,
        type: StudyParameterType,
        callback: OnReturnCallback<List<StudyParameter>>
    )

    fun getIsInvertYAxis(callback: OnReturnCallback<Boolean>)

    fun setIsInvertYAxis(inverted: Boolean)

    fun getIsExtendedHours(callback: OnReturnCallback<Boolean>)

    fun setExtendedHours(extended: Boolean)
}
