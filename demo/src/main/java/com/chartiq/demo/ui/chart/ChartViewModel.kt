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
    private val networkManager: NetworkManager,
    private val applicationPrefs: ApplicationPrefs
) : ViewModel() {

    val currentSymbol = MutableLiveData<Symbol>()

    val chartInterval = MutableLiveData<Interval>()

    val drawingTool = MutableLiveData<DrawingTool>()

    val resultLiveData = MutableLiveData<ChartData>()

    val errorLiveData = MutableLiveData<Unit>()

    // TODO: 19.10.20 Review
    fun getDataFeed(params: QuoteFeedParams, callback: DataSourceCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = networkManager.fetchDataFeed(params)) {
                is NetworkResult.Success -> resultLiveData
                    .postValue(ChartData(result.data, callback))
                is NetworkResult.Failure -> errorLiveData
                    .postValue(Unit)
            }
        }
    }

    fun fetchSavedSettings() {
        currentSymbol.value = applicationPrefs.getChartSymbol()
        chartInterval.value = applicationPrefs.getChartInterval()
        drawingTool.value = applicationPrefs.getDrawingTool()
    }

    class ChartViewModelFactory(
        private val argNetworkManager: NetworkManager,
        private val argApplicationPrefs: ApplicationPrefs
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(NetworkManager::class.java, ApplicationPrefs::class.java)
                .newInstance(argNetworkManager, argApplicationPrefs)
        }
    }
}
