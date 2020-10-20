package com.chartiq.demo.ui.chart

import androidx.lifecycle.*
import com.chartiq.demo.network.NetworkManager
import com.chartiq.demo.network.NetworkResult
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.QuoteFeedParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChartViewModel(private val networkManager: NetworkManager) : ViewModel() {

    val resultLiveData: LiveData<ChartData>
        get() = mResultLiveData
    private val mResultLiveData = MutableLiveData<ChartData>()

    val errorLiveData: LiveData<Unit>
        get() = mErrorLiveData
    private val mErrorLiveData = MutableLiveData<Unit>()

    // TODO: 19.10.20 Review
    fun getDataFeed(params: QuoteFeedParams, callback: DataSourceCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = networkManager.fetchDataFeed(params)) {
                is NetworkResult.Success -> mResultLiveData
                    .postValue(ChartData(result.data, callback))
                is NetworkResult.Failure -> mErrorLiveData
                    .postValue(Unit)
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
