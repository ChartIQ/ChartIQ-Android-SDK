package com.chartiq.demo.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ui.settings.chartstyle.ChartTypeModel
import com.chartiq.demo.ui.settings.chartstyle.toModel
import com.chartiq.demo.ui.settings.language.ChartIQLanguage
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.ChartIQScale
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.charttype.ChartType

class SettingsViewModel(
    private val chartIQ: ChartIQ,
    private val applicationPrefs: ApplicationPrefs
) : ViewModel() {

    val chartStyle = MutableLiveData<ChartTypeModel>()
    val language = MutableLiveData<ChartIQLanguage>(ChartIQLanguage.EN)
    val logScale = MutableLiveData<Boolean>(false)
    val invertYAxis = MutableLiveData<Boolean>(false)
    val extendHours = MutableLiveData<Boolean>(false)

    init {
        initChartStyle()
        initChartPreferences()
        initChartLanguage()
    }

    private fun initChartLanguage() {
        val saveLanguage = applicationPrefs.getLanguage()
        language.value = saveLanguage
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
        chartIQ.getAggregationChartType { aggregationChartType ->
            if (aggregationChartType != null) {
                chartStyle.postValue(aggregationChartType.toModel())
            } else {
                chartIQ.getChartType { chartType ->
                    chartStyle.postValue(chartType.toModel())
                }
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

    fun updateChartStyle(chartStyle: ChartTypeModel) {
        if (ChartType.values().any { it.name == chartStyle.name }) {
            val selectedChartType = ChartType.valueOf(chartStyle.name)
            chartIQ.setChartType(selectedChartType)
        } else if (AggregationChartType.values().any { it.name == chartStyle.name }) {
            val selectedAggregationChartType = AggregationChartType.valueOf(chartStyle.name)
            chartIQ.setAggregationType(selectedAggregationChartType)
        }
        initChartStyle()

    }

    fun updateLanguage(iqLanguage: ChartIQLanguage) {
        applicationPrefs.setLanguage(iqLanguage)
        language.value = iqLanguage
    }

    class ViewModelFactory(
        private val argChartIQ: ChartIQ,
        private val applicationPrefs: ApplicationPrefs
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java, ApplicationPrefs::class.java)
                .newInstance(argChartIQ, applicationPrefs)
        }
    }
}
