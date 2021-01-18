package com.chartiq.demo.ui.chart.comparison

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.Series

class CompareViewModel(private val chartIQHandler: ChartIQ) : ViewModel() {

    val series = MutableLiveData<List<Series>>()

    init {
        getSeries()
    }

    fun addSeries(symbolName: String, color: String) {
        chartIQHandler.addSeries(Series(symbolName, color), true)
        getSeries()
    }

    fun removeSeries(series: Series) {
        chartIQHandler.removeSeries(series.symbolName)
        getSeries()
    }

    fun updateSeriesParameter(symbolName: String, parameter: String, value: String) {
        chartIQHandler.setSeriesParameter(symbolName, parameter, value)
        getSeries()
    }

    private fun getSeries() {
        chartIQHandler.getActiveSeries {
            series.value = it
        }
    }

    class ViewModelFactory(
        private val chartIQHandler: ChartIQ
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java)
                .newInstance(chartIQHandler)
        }
    }
}
