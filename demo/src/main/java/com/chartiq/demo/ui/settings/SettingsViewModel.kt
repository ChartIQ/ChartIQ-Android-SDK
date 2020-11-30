package com.chartiq.demo.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.ChartIQScale

class SettingsViewModel(
    val chartIQ: ChartIQ
) : ViewModel() {

    val chartStyle = MutableLiveData<String>("Candle")
    val logScale = MutableLiveData<Boolean>(false)
    val invertYAxis = MutableLiveData<Boolean>(false)
    val extendHours = MutableLiveData<Boolean>(false)

    init {
        initChartStyle()
        initChartPreferences()
    }

    private fun initChartPreferences() {
        chartIQ.getChartScale {
            logScale.value = it == ChartIQScale.LINEAR
        }
        chartIQ.getIsInvertYAxis {
            invertYAxis.value = it
        }
        chartIQ.getIsExtendedHours {
            extendHours.value = it
        }
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
        chartIQ.setChartScale(if (enabled) ChartIQScale.LINEAR else ChartIQScale.LOG)
        initChartPreferences()
    }

    fun changeInvertY(enabled: Boolean) {
        chartIQ.setIsInvertYAxis(enabled)
        initChartPreferences()
    }

    fun changeExtendHours(enabled: Boolean) {
        chartIQ.setExtendedHours(enabled)
        initChartPreferences()
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
