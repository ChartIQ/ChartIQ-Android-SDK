package com.chartiq.demo.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQ

class SettingsViewModel(
    val chartIQ: ChartIQ
) : ViewModel() {

    val chartStyle = MutableLiveData<String>("Candle")
    val logScale = MutableLiveData<Boolean>(false)
    val invertYAxis = MutableLiveData<Boolean>(false)
    val extendHours = MutableLiveData<Boolean>(false)

    init {
        initChartStyle()
    }

    private fun initChartStyle() {
        chartIQ.getChartType {
            chartStyle.value = it.value
        }
        chartIQ.getAggregationChartType {
            it?.let {
                chartStyle.value = it.value
            }
        }
    }

    fun changeLogScale(enabled: Boolean) {
        //todo ("Not yet implemented")
    }

    fun changeInvertY(enabled: Boolean) {
        //todo("Not yet implemented")
    }

    fun changeExtendHours(enabled: Boolean) {
        //todo ("Not yet implemented")
    }

    class ViewModelFactory(
        private val argChartIQ: ChartIQ
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java)
                .newInstance(argChartIQ)
        }
    }
}
