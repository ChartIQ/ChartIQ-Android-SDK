package com.chartiq.demo.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartiq.demo.network.NetworkManager
import com.chartiq.demo.network.NetworkResult
import com.chartiq.sdk.model.OHLCParams
import com.chartiq.sdk.model.QuoteFeedParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChartViewModel(private val networkManager: NetworkManager) : ViewModel() {

    fun getDataFeed(params: QuoteFeedParams, callback: (list: List<OHLCParams>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = networkManager.fetchDataFeed(params)) {
                is NetworkResult.Success -> callback(result.data)
                is NetworkResult.Failure -> Unit // TODO: 19.10.20 Add failure handling
            }
        }
    }

    class ChartViewModelFactory(private val argNetworkManager: NetworkManager) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(NetworkManager::class.java)
                .newInstance(argNetworkManager)
        }
    }
}
