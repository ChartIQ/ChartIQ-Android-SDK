package com.chartiq.demo.ui.chart.searchsymbol

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.network.NetworkManager
import com.chartiq.demo.network.NetworkResult
import com.chartiq.demo.network.model.SymbolResponse
import com.chartiq.demo.ui.chart.searchsymbol.list.SearchResultItem
import com.chartiq.demo.util.Event
import kotlinx.coroutines.*

class SearchSymbolViewModel(private val networkManager: NetworkManager) : ViewModel() {

    private var searchJob: Job? = null

    private val filter = MutableLiveData(SearchFilter.ALL)

    val resultLiveData = MutableLiveData<List<SearchResultItem>>()

    val errorLiveData = MutableLiveData<Event<Unit>>()

    val query = MutableLiveData("")

    val isLoading = MutableLiveData(false)

    fun updateQuery(symbol: String) {
        query.value = symbol
        fetchSymbol()
    }

    fun updateFilter(filter: SearchFilter) {
        this.filter.value = filter
        query.value = query.value
        fetchSymbol()
    }

    private fun fetchSymbol() {
        searchJob?.cancel()
        val isEmptySymbol = query.value.isNullOrEmpty()
        isLoading.value = !isEmptySymbol
        if (isEmptySymbol) {
            resultLiveData.value = listOf()
        } else {
            fetchSymbolWithDebounce(query.value!!)
        }
    }

    private fun SymbolResponse.mapToItemList(): List<SearchResultItem> = payload.symbols
        .map { element -> element.split('|') }
        .map { SearchResultItem(it[0], it[1], it[2]) }

    private fun fetchSymbolWithDebounce(symbol: String) {
        searchJob?.cancel()
        searchJob = CoroutineScope(Dispatchers.IO).launch {
            delay(DELAY_SEARCH)
            if (isActive) {
                when (val result = networkManager.fetchSymbol(symbol, filter.value?.value)) {
                    is NetworkResult.Success -> resultLiveData.postValue(result.data.mapToItemList())
                    is NetworkResult.Failure -> errorLiveData.postValue(Event(Unit))
                }
                isLoading.postValue(false)

            }
        }
    }

    companion object {
        private const val DELAY_SEARCH = 300L
    }

    class SearchViewModelFactory(private val argNetworkManager: NetworkManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(NetworkManager::class.java)
                .newInstance(argNetworkManager)
        }
    }
}
