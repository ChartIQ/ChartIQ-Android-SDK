package com.chartiq.demo.ui

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
import java.util.*

class MainViewModel(
    private val networkManager: NetworkManager,
    private val applicationPrefs: ApplicationPrefs,
    private val chartIQ: ChartIQ,
) : ViewModel() {

    val activeStudies = MutableLiveData<List<Study>>()

    val errorLiveData = MutableLiveData<Unit>()

    val isNavBarVisible = MutableLiveData(true)

    init {
        chartIQ.apply {
            start {
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
                setupChart()
            }
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
        private val argChartIQHandler: ChartIQ
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(
                    NetworkManager::class.java,
                    ApplicationPrefs::class.java,
                    ChartIQ::class.java
                )
                .newInstance(argNetworkManager, argApplicationPrefs, argChartIQHandler)
        }
    }
}
