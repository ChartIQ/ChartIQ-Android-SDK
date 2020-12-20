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
import com.chartiq.demo.network.NetworkManager
import com.chartiq.demo.network.NetworkResult
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.DataSource
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.QuoteFeedParams
import com.chartiq.sdk.model.study.Study
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val networkManager: NetworkManager,
    private val applicationPrefs: ApplicationPrefs,
    private val chartIQ: ChartIQ,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    val activeStudies = MutableLiveData<List<Study>>()

    val errorLiveData = MutableLiveData<Unit>()

    val isNavBarVisible = MutableLiveData(true)

    val isNetworkAvailable = MutableLiveData(false)

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
            start {}
        }
    }

    fun showNavBar(show: Boolean) {
        isNavBarVisible.value = show
    }

    fun fetchActiveStudyData() {
        chartIQ.getActiveStudies { result ->
            activeStudies.value = result
        }
    }

    fun setupChart() {
        val symbol = applicationPrefs.getChartSymbol()
        chartIQ.setSymbol(symbol.value)
        chartIQ.setDataMethod(DataMethod.PULL, symbol.value)

        val interval = applicationPrefs.getChartInterval()
        chartIQ.setPeriodicity(
            interval.getPeriod(),
            interval.getInterval(),
            interval.getTimeUnit()
        )
    }

    fun hasInternetConnection() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            isNetworkAvailable.value = connectivityManager.activeNetworkInfo != null
        } else {
            val allNetworks: Array<Network> = connectivityManager.allNetworks
            for (network in allNetworks) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                if (networkCapabilities != null) {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    ) {
                        isNetworkAvailable.value = true
                        return
                    }
                }
            }
            isNetworkAvailable.value = false
        }
    }

    private fun loadChartData(params: QuoteFeedParams, callback: DataSourceCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            val applicationId = applicationPrefs.getApplicationId()
            when (val result = networkManager.fetchDataFeed(params, applicationId)) {
                is NetworkResult.Success -> {
                    withContext(Dispatchers.Main) { callback.execute(result.data) }
                }
                is NetworkResult.Failure -> errorLiveData.postValue(Unit)

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
