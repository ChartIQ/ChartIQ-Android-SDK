package com.chartiq.sdk

import android.content.Context
import android.view.View
import com.chartiq.sdk.model.ChartScale
import com.chartiq.sdk.model.CrosshairHUD
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.charttype.ChartType
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.sdk.model.study.ChartIQStudy

import java.util.*

interface ChartIQ : ChartIQDrawingTool, ChartIQStudy {

    val chartView: View

    /**
     * Sets a DataSource for ChartIQ. Note that it should be set before calling [ChartIQ.start]
     */
    fun setDataSource(dataSource: DataSource)

    fun start(onStartCallback: OnStartCallback)

    fun setSymbol(symbol: String)


    fun setDataMethod(method: DataMethod, symbol: String)

    fun enableCrosshairs()

    fun disableCrosshairs()

    fun setPeriodicity(period: Int, interval: String, timeUnit: String)

    fun setAggregationType(aggregationType: AggregationChartType)

    fun setChartType(chartType: ChartType)

    fun getChartType(callback: OnReturnCallback<ChartType>)

    fun getAggregationChartType(callback: OnReturnCallback<AggregationChartType?>)

    fun getChartScale(callback: OnReturnCallback<ChartScale>)

    fun setChartScale(scale: ChartScale)

    fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>)

    fun getIsInvertYAxis(callback: OnReturnCallback<Boolean>)

    fun setIsInvertYAxis(inverted: Boolean)

    fun getIsExtendedHours(callback: OnReturnCallback<Boolean>)

    fun setExtendedHours(extended: Boolean)

    fun getHUDDetails(callback: OnReturnCallback<CrosshairHUD>)

    fun getTranslations(languageCode: String, callback: OnReturnCallback<Map<String, String>>)

    fun setLanguage(languageCode: String)

    companion object {
        fun getInstance(url: String, context: Context): ChartIQ {
            return ChartIQHandler(url, context)
        }
    }
}
