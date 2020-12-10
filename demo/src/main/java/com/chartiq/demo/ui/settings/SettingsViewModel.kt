package com.chartiq.demo.ui.settings

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ui.settings.chartstyle.ChartTypeModel
import com.chartiq.demo.ui.settings.chartstyle.toModel
import com.chartiq.demo.ui.settings.language.ChartIQLanguage
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.ChartIQScale
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.charttype.ChartType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

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
        initChartScale()
        initChartYAxis()
        initChartHours()
        initChartLanguage()
    }

    private fun initChartLanguage() {
        viewModelScope.launch(Dispatchers.Main) {
            applicationPrefs.languageState.collect { saveLanguage ->
                val languageCode = saveLanguage.name.toLowerCase(Locale.ENGLISH)
                chartIQ.setLanguage(languageCode)
                language.value = saveLanguage
                //todo call if there's no translations only
                chartIQ.getTranslations(languageCode) {
                    Log.i(SettingsViewModel::class.simpleName, it.toString())
                }

            }
        }
    }

    private fun initChartScale() {
        chartIQ.getChartScale {
            logScale.value = it == ChartIQScale.LINEAR
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
        chartIQ.setChartScale(
            if (enabled) {
                ChartIQScale.LINEAR
            } else {
                ChartIQScale.LOG
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
