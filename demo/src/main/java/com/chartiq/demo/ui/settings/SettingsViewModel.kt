package com.chartiq.demo.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ui.settings.chartstyle.ChartTypeItem
import com.chartiq.demo.ui.settings.chartstyle.toModel
import com.chartiq.demo.ui.settings.language.ChartIQLanguage
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.ChartScale
import com.chartiq.sdk.model.charttype.ChartAggregationType
import com.chartiq.sdk.model.charttype.ChartType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val chartIQ: ChartIQ,
    private val applicationPrefs: ApplicationPrefs
) : ViewModel() {

    val language = MutableLiveData<ChartIQLanguage>(ChartIQLanguage.EN)
    val chartStyle = MutableLiveData<ChartTypeItem>()
    val logScale = MutableLiveData<Boolean>(false)
    val invertYAxis = MutableLiveData<Boolean>(false)
    val extendHours = MutableLiveData<Boolean>(false)

    init {
        initChartStyle()
        initChartScale()
        initChartYAxis()
        initChartHours()
        initChartLanguage()
    }

    private fun initChartLanguage() {
        viewModelScope.launch(Dispatchers.Main) {
            applicationPrefs.languageState.collect { saveLanguage ->
                language.value = saveLanguage
                //to perform new translations for selected chart value
                initChartStyle()
            }
        }
    }

    private fun initChartScale() {
        chartIQ.getChartScale {
            logScale.value = it == ChartScale.LOG
        }
    }

    private fun initChartYAxis() {
        chartIQ.getIsInvertYAxis {
            invertYAxis.value = it
        }
    }

    private fun initChartHours() {
        chartIQ.getIsExtendedHours {
            extendHours.value = it
        }
    }

    private fun initChartStyle() {
        chartIQ.getChartAggregationType { aggregationChartType ->
            if (aggregationChartType != null) {
                chartStyle.postValue(aggregationChartType.toModel())
            } else {
                chartIQ.getChartType { chartType ->
                    chartType?.let { chartStyle.postValue(it.toModel()) }
                }
            }
        }
    }

    fun changeLogScale(enabled: Boolean) {
        chartIQ.setChartScale(
            if (enabled) {
                ChartScale.LOG
            } else {
                ChartScale.LINEAR
            }
        )
        initChartScale()
    }

    fun changeInvertY(enabled: Boolean) {
        chartIQ.setIsInvertYAxis(enabled)
        initChartYAxis()
    }

    fun changeExtendHours(enabled: Boolean) {
        chartIQ.setExtendedHours(enabled)
        initChartHours()
    }

    fun updateChartStyle(chartStyle: ChartTypeItem) {
        if (ChartType.values().any { it.name == chartStyle.name }) {
            val selectedChartType = ChartType.valueOf(chartStyle.name)
            chartIQ.setChartType(selectedChartType)
        } else if (ChartAggregationType.values().any { it.name == chartStyle.name }) {
            val selectedAggregationChartType = ChartAggregationType.valueOf(chartStyle.name)
            chartIQ.setAggregationType(selectedAggregationChartType)
        }
        initChartStyle()

    }

    fun updateLanguage(iqLanguage: ChartIQLanguage) {
        applicationPrefs.setLanguage(iqLanguage)
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
