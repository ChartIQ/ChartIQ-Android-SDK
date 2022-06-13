package com.chartiq.demo.ui

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.localization.RemoteTranslations
import com.chartiq.demo.network.NetworkManager
import com.chartiq.demo.network.NetworkResult
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.searchsymbol.Symbol
import com.chartiq.demo.ui.settings.chartstyle.ChartTypeItem
import com.chartiq.demo.ui.settings.chartstyle.toModel
import com.chartiq.demo.util.Event
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.DataSource
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.ChartTheme
import com.chartiq.sdk.model.QuoteFeedParams
import com.chartiq.sdk.model.charttype.ChartAggregationType
import com.chartiq.sdk.model.charttype.ChartType
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.sdk.model.study.Study
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainViewModel(
    private val networkManager: NetworkManager,
    private val applicationPrefs: ApplicationPrefs,
    private val chartIQ: ChartIQ,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    val activeStudies = MutableLiveData<List<Study>>()

    val errorLiveData = MutableLiveData<Unit>()

    val currentLocaleEvent = MutableLiveData<Event<RemoteTranslations>>()

    val networkIsAvailableEvent = MutableLiveData<Event<Boolean>>()

    private val chartTheme = MutableLiveData<Event<ChartTheme>>()

    val symbol = MutableLiveData<Symbol>()

    val interval = MutableLiveData<Interval>()

    val chartStyle = MutableLiveData<ChartTypeItem>()


    init {
        prepareSession()
        chartIQ.apply {
            setDataSource(object : DataSource {
                override fun pullInitialData(
                    params: QuoteFeedParams,
                    callback: DataSourceCallback,
                ) {
                    loadChartData(params, callback)
                }

                override fun pullUpdateData(
                    params: QuoteFeedParams,
                    callback: DataSourceCallback,
                ) {
                    loadChartData(params, callback)
                }

                override fun pullPaginationData(
                    params: QuoteFeedParams,
                    callback: DataSourceCallback,
                ) {
                    loadChartData(params, callback)
                }
            })

            start {
                observeLocalization()

                val theme = chartTheme.value?.peekContent() ?: ChartTheme.DAY
                chartIQ.setTheme(theme)
                setupChart()
                getChartStyle()
            }
        }
    }

    fun fetchActiveStudyData() {
        chartIQ.getActiveStudies { result ->
            activeStudies.value = result
        }
    }

    fun checkInternetAvailability() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            if (connectivityManager.activeNetworkInfo != null) {
                if (networkIsAvailableEvent.value?.peekContent() != true) {
                    reloadData()
                    networkIsAvailableEvent.value = Event(true)
                }
            } else {
                networkIsAvailableEvent.value = Event(false)
            }
        } else {
            val allNetworks: Array<Network> = connectivityManager.allNetworks
            for (network in allNetworks) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                if (networkCapabilities != null) {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    ) {
                        if (networkIsAvailableEvent.value?.peekContent() != true) {
                            reloadData()
                            networkIsAvailableEvent.value = Event(true)
                        }
                        return
                    }
                }
            }
            networkIsAvailableEvent.value = Event(false)
        }
    }

    fun updateTheme(theme: ChartTheme) {
        chartTheme.value = Event(theme)
        chartIQ.setTheme(theme)
    }

    private fun prepareSession() {
        // Reset Drawing Tool
        applicationPrefs.saveDrawingTool(DrawingTool.NONE)
    }

    fun updateSymbol(symbol: Symbol) {
        chartIQ.setSymbol(symbol.value)
        setupChart()
    }

    fun updateInterval(interval: Interval) {
        chartIQ.setPeriodicity(
            interval.period,
            interval.getInterval(),
            interval.getSafeTimeUnit(),
        )
        setupChart()
    }

    fun updateChartStyle(chartStyle: ChartTypeItem) {
        if (ChartType.values().any { it.name == chartStyle.name }) {
            val selectedChartType = ChartType.valueOf(chartStyle.name)
            chartIQ.setChartType(selectedChartType)
        } else if (ChartAggregationType.values().any { it.name == chartStyle.name }) {
            val selectedAggregationChartType = ChartAggregationType.valueOf(chartStyle.name)
            chartIQ.setAggregationType(selectedAggregationChartType)
        }
        getChartStyle()
    }

    private fun reloadData() {
        symbol.value?.let {
            updateSymbol(it)
        }
        fetchActiveStudyData()
    }

    private fun setupChart() {
        with(chartIQ) {
            getSymbol {
                val currentSymbol = Symbol(it)
                if (symbol.value != currentSymbol) {
                    symbol.value = currentSymbol
                }
            }
            getPeriodicity { periodicity ->
                getInterval { cInterval ->
                    getTimeUnit { timeUnit ->
                        val currentInterval =
                            Interval.createInterval(periodicity, cInterval, timeUnit)
                        if (interval.value != currentInterval) {
                            interval.value = currentInterval
                        }
                    }
                }
            }
        }
    }

    private fun loadChartData(params: QuoteFeedParams, callback: DataSourceCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            val applicationId = applicationPrefs.getApplicationId()
            when (val result = networkManager.fetchDataFeed(params, applicationId)) {
                is NetworkResult.Success -> {
                    withContext(Dispatchers.Main) { callback.execute(result.data) }
                }
                is NetworkResult.Failure -> {
                    withContext(Dispatchers.Main) {
                        checkInternetAvailability()
                    }
                }
            }
        }
    }

    private fun observeLocalization() {
        viewModelScope.launch(Dispatchers.IO) {
            applicationPrefs.languageState.collect {
                withContext(Dispatchers.Main) {
                    val locale = Locale(it.code)
                    chartIQ.setLanguage(it.code)
                    chartIQ.getTranslations(it.code) { translationsMap ->
                        currentLocaleEvent.postValue(
                            Event(
                                RemoteTranslations(
                                    locale,
                                    translationsMap
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getChartStyle() {
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

    class ViewModelFactory(
        private val argNetworkManager: NetworkManager,
        private val argApplicationPrefs: ApplicationPrefs,
        private val argChartIQHandler: ChartIQ,
        private val argConnectivityManager: ConnectivityManager
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(
                    NetworkManager::class.java,
                    ApplicationPrefs::class.java,
                    ChartIQ::class.java,
                    ConnectivityManager::class.java
                )
                .newInstance(
                    argNetworkManager,
                    argApplicationPrefs,
                    argChartIQHandler,
                    argConnectivityManager
                )
        }
    }
}
