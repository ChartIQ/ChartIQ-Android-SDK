package com.chartiq.demo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQ

class SettingsViewModel(
    val chartIQ: ChartIQ
) : ViewModel() {

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
