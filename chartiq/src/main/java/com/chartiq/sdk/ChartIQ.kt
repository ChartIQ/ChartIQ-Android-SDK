package com.chartiq.sdk

import android.content.Context
import android.view.View
import com.chartiq.sdk.model.ChartScale
import com.chartiq.sdk.model.CrosshairHUD
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.charttype.ChartType
import com.chartiq.sdk.model.study.ChartIQStudy

import java.util.*

interface ChartIQ : ChartIQDrawingTool, ChartIQStudy {
    /**
     * A ChartIQ [View] that represents a ChartIQ UI
     */
    val chartView: View

    /**
     * Sets a DataSource for ChartIQ. Note that it should be set before calling [ChartIQ.start]
     */
    fun setDataSource(dataSource: DataSource)

    /**
     * Starts a ChartIQ WebClient initialization
     * @param onStartCallback A callback to subscribe to a successful WebClient initialization
     */
    fun start(onStartCallback: OnStartCallback)

    fun setSymbol(symbol: String)

    fun setDataMethod(method: DataMethod, symbol: String)

    fun enableCrosshairs()

    fun disableCrosshairs()

    fun setPeriodicity(period: Int, interval: String, timeUnit: String)

    /**
     * Sets an Aggregation type for charts
     * @param aggregationType A selected aggregation type
     */
    fun setAggregationType(aggregationType: AggregationChartType)

    /**
     * Sets an chart type for charts
     * @param chartType A selected chart type
     */
    fun setChartType(chartType: ChartType)

    /**
     * Gets a selected chart type
     * @param callback A callback to subscribe on to get a chart type
     */
    fun getChartType(callback: OnReturnCallback<ChartType>)

    /**
     * Gets a selected aggregation chart type
     * @param callback A callback to subscribe on to get an aggregation chart type
     */
    fun getAggregationChartType(callback: OnReturnCallback<AggregationChartType?>)

    /**
     * Gets a selected chart scale
     * @param callbackA callback to subscribe on to get a selected chart scale
     */
    fun getChartScale(callback: OnReturnCallback<ChartScale>)

    /**
     * Sets an chart scale for charts
     * @param scale A selected chart scale
     */
    fun setChartScale(scale: ChartScale)

    fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>)

    /**
     * Gets a selected chart Y axis invertion
     * @param callback callback to subscribe on to get a selected Y-axis invertion.
     */
    fun getIsInvertYAxis(callback: OnReturnCallback<Boolean>)

    /**
     * Sets a chart Y axis invertion
     * @param inverted  A selected invertion value. if true, Y axis is inverted
     */
    fun setIsInvertYAxis(inverted: Boolean)

    /**
     * Gets a selected extended hours flag value
     * @param callback callback to subscribe on to get a  extended hours flag value
     * If true, a chart uses extended hours
     */
    fun getIsExtendedHours(callback: OnReturnCallback<Boolean>)

    /**
     * Sets a chart extended hours flag value
     * @param extended  A selected boolean extended hours value. if true, extended hours are applied
     */
    fun setExtendedHours(extended: Boolean)

    fun getHUDDetails(callback: OnReturnCallback<CrosshairHUD>)

    fun getTranslations(languageCode: String, callback: OnReturnCallback<Map<String, String>>)

    fun setLanguage(languageCode: String)

    fun addMeasureListener(measureCallback: MeasureCallback)

    companion object {
        fun getInstance(url: String, context: Context): ChartIQ {
            return ChartIQHandler(url, context)
        }
    }
}
