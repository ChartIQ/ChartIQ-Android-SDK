package com.chartiq.demo.ui.chart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.network.NetworkManager
import com.chartiq.demo.network.NetworkResult
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.searchsymbol.Symbol
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.QuoteFeedParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChartViewModel(
    private val applicationPrefs: ApplicationPrefs,
    private val networkManager: NetworkManager
) : ViewModel() {

    val currentSymbol = MutableLiveData<Symbol>()

    val chartInterval = MutableLiveData<Interval>()

    val drawingTool = MutableLiveData<DrawingTool>()

    @Deprecated("This logic was moved to MainViewModel class")
    val resultLiveData = MutableLiveData<ChartData>()

    @Deprecated("This logic was moved to MainViewModel class")
    val errorLiveData = MutableLiveData<Unit>()

    init {
        fetchSavedSettings()
    }

    // TODO: 19.10.20 Review
    @Deprecated("All state data that should be kept during the whole app live  and should not be attached to a concrete fragment is moved to MainViewModel")
    fun getDataFeed(params: QuoteFeedParams, callback: DataSourceCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            val applicationId = applicationPrefs.getApplicationId()
            when (val result = networkManager.fetchDataFeed(params, applicationId)) {
                is NetworkResult.Success -> resultLiveData
                    .postValue(ChartData(result.data, callback))
                is NetworkResult.Failure -> errorLiveData
                    .postValue(Unit)
            }
        }
    }

    private fun fetchSavedSettings() {
        currentSymbol.value = applicationPrefs.getChartSymbol()
        chartInterval.value = applicationPrefs.getChartInterval()
        drawingTool.value = applicationPrefs.getDrawingTool()
    }

    class ChartViewModelFactory(
        private val argApplicationPrefs: ApplicationPrefs,
        private val argNetworkManager: NetworkManager
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ApplicationPrefs::class.java, NetworkManager::class.java)
                .newInstance(argApplicationPrefs, argNetworkManager)
        }
    }
}
