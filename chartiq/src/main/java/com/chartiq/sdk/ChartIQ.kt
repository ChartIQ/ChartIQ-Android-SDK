package com.chartiq.sdk

import android.content.Context
import android.view.View
import com.chartiq.sdk.model.*
import com.chartiq.sdk.model.drawingtool.DrawingTool
import java.util.*

interface ChartIQ: ChartIQDrawingTool, ChartIQStudy {

    val chartView: View

    fun start(onStartCallback: OnStartCallback)

    fun setSymbol(symbol: String)

    fun setDataSource(dataSource: DataSource)

    fun setDataMethod(method: DataMethod, symbol: String)

    fun enableCrosshairs()

    fun disableCrosshairs()

    fun setPeriodicity(period: Int, interval: String, timeUnit: String)

    fun setAggregationType(aggregationType: AggregationType)

    fun setChartType(chartType: ChartType)

    fun setChartScale(scale: ChartScale)

    fun setOHLCParameters(talkbackFields: HashMap<String, Boolean>)

    fun getHUDDetails(callback: OnReturnCallback<CrosshairHUD>)

    fun restoreDefaultDrawingConfig(tool: DrawingTool, all: Boolean)

    companion object {
        fun getInstance(url: String, context: Context): ChartIQ {
            return ChartIQHandler(url, context)
        }
    }
}
