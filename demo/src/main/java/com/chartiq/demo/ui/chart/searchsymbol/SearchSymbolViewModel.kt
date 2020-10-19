package com.chartiq.demo.ui.chart.searchsymbol

import androidx.lifecycle.*
import com.chartiq.demo.network.NetworkManager
import com.chartiq.demo.network.NetworkResult
import com.chartiq.demo.network.model.SymbolResponse
import com.chartiq.demo.ui.chart.searchsymbol.list.SearchResultItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchSymbolViewModel(private val networkManager: NetworkManager) : ViewModel() {

    val resultLiveData: LiveData<List<SearchResultItem>>
        get() = mResultLiveData
    private val mResultLiveData = MutableLiveData<List<SearchResultItem>>()

    val errorLiveData: LiveData<Unit>
        get() = mErrorLiveData
    private val mErrorLiveData = MutableLiveData<Unit>()

    fun fetchSymbol(symbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = networkManager.fetchSymbol(symbol)) {
                is NetworkResult.Success -> mResultLiveData.postValue(result.data.mapToItemList())
                is NetworkResult.Failure -> mErrorLiveData.postValue(Unit)
            }
        }
    }

    private fun SymbolResponse.mapToItemList(): List<SearchResultItem> = payload.symbols
        .map { element -> element.split('|') }
        .map { SearchResultItem(it[0], it[1], it[2]) }

    class SearchViewModelFactory(private val argNetworkManager: NetworkManager) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(NetworkManager::class.java)
                .newInstance(argNetworkManager)
        }
    }
}
