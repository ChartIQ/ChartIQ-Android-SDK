package com.chartiq.demo.ui

import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.localization.RemoteTranslations
import com.chartiq.demo.network.NetworkException
import com.chartiq.demo.network.NetworkManager
import com.chartiq.demo.network.NetworkResult
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.searchsymbol.Symbol
import com.chartiq.demo.util.Event
import com.chartiq.demo.util.combineLatest
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.DataSource
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.ChartTheme
import com.chartiq.sdk.model.QuoteFeedParams
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

    private val isNavBarAlwaysVisible = MutableLiveData(false)

    private val currentOrientation = MutableLiveData(Configuration.ORIENTATION_PORTRAIT)

    private val isFullView = MutableLiveData(false)

    val isNavBarVisible: LiveData<Boolean> =
            Transformations.map(
                    combineLatest(
                            isNavBarAlwaysVisible,
                            currentOrientation,
                            isFullView
                    )
            ) { (isVisible, orientation, isFull) ->
                return@map if (isVisible == true) {
                    true
                } else {
                    orientation == Configuration.ORIENTATION_PORTRAIT && isFull == true
                }
            }

    val currentLocaleEvent = MutableLiveData<Event<RemoteTranslations>>()

    val networkIsAvailableEvent = MutableLiveData<Event<Boolean>>()

    private val chartTheme = MutableLiveData<Event<ChartTheme>>()

    val symbol = MutableLiveData<Symbol>()

    val interval = MutableLiveData<Interval>()

    val isFullscreen = MutableLiveData(false)

    init {
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
            }
        }
    }

    fun setFullscreen(isLandscape: Boolean) {
        isFullscreen.value = isLandscape
    }

    fun updateFullView(isFullView: Boolean) {
        this.isFullView.postValue(isFullView)
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

    fun prepareSession() {
        // Reset Drawing Tool
        applicationPrefs.saveDrawingTool(DrawingTool.NONE)
    }

    fun setAlwaysOnDisplayNavBar(alwaysOnDisplay: Boolean) {
        isNavBarAlwaysVisible.postValue(alwaysOnDisplay)
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
                    when (result.exception) {
                        is NetworkException -> withContext(Dispatchers.Main) {
                            checkInternetAvailability()
                        }
                        else -> errorLiveData.postValue(Unit)
                    }
                }

            }
        }
    }

    private fun observeLocalization() {
        viewModelScope.launch(Dispatchers.IO) {
            applicationPrefs.languageState.collect {
                withContext(Dispatchers.Main) {
                    val locale = Locale(it.name.toLowerCase(Locale.ENGLISH))
                    chartIQ.setLanguage(it.name.toLowerCase(Locale.ENGLISH))
                    chartIQ.getTranslations(it.name.toLowerCase(Locale.ENGLISH)) { translationsMap ->
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
