package com.chartiq.demo.ui.chart.comparison

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.ui.common.colorpicker.toHexStringWithHash
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.Series

class CompareViewModel(private val chartIQHandler: ChartIQ) : ViewModel() {

    val series = MutableLiveData<List<Series>>()
    private val colors: MutableList<Int> = mutableListOf()
    private var currentColorIndex: Int = 0

    init {
        getSeries()
        setupColorIndex()
    }

    fun setSeriesColors(seriesColors: List<Int>) {
        colors.addAll(seriesColors)
    }

    private fun setupColorIndex() {
        val color: String? = series.value?.lastOrNull()?.color
        val index = color?.let { c ->
            colors.indexOfFirst { it.toHexStringWithHash() == c }
        }
        if (index != null && index != -1) {
            currentColorIndex = index + 1
        }
    }

    private fun nextColor(): String {
        if (currentColorIndex > colors.size - 1) {
            currentColorIndex = 0
        }
        val nextColor = colors[currentColorIndex]
        currentColorIndex += 1
        return nextColor.toHexStringWithHash();
    }

    fun addSeries(symbolName: String) {
        val color = nextColor()
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
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java)
                .newInstance(chartIQHandler)
        }
    }
}
