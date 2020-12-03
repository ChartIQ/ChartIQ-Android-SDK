package com.chartiq.demo.ui.chart.searchsymbol

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.network.NetworkManager
import com.chartiq.demo.network.NetworkResult
import com.chartiq.demo.network.model.SymbolResponse
import com.chartiq.demo.ui.chart.searchsymbol.list.SearchResultItem
import com.chartiq.demo.util.Event
import kotlinx.coroutines.*

class SearchSymbolViewModel(
    private val networkManager: NetworkManager,
    private val argPrefs: ApplicationPrefs
) : ViewModel() {

    private val searchJob = SupervisorJob()

    private val searchScope = CoroutineScope(searchJob + Dispatchers.IO)

    val resultLiveData = MutableLiveData<List<SearchResultItem>>()

    val errorLiveData = MutableLiveData<Event<Unit>>()

    val query = MutableLiveData("")

    val filter = MutableLiveData(SearchFilter.ALL)

    val isLoading = MutableLiveData(false)

    fun fetchSymbol(symbol: String) {
        searchJob.cancelChildren()
        isLoading.value = true
        query.value = symbol
        if (symbol.isEmpty()) {
            resultLiveData.value = listOf()
            isLoading.value = false
            return
        }
        searchScope.launch {
            when (val result = networkManager.fetchSymbol(symbol, filter.value?.value)) {
                is NetworkResult.Success -> resultLiveData.postValue(result.data.mapToItemList())
                is NetworkResult.Failure -> errorLiveData.postValue(Event(Unit))
            }
            isLoading.postValue(false)
        }
    }

    fun updateFilter(filter: SearchFilter) {
        this.filter.value = filter
        query.value?.let {
            fetchSymbol(it)
        }
    }

    fun saveSymbol() {
        argPrefs.saveChartSymbol(Symbol(query.value!!))
    }

    private fun SymbolResponse.mapToItemList(): List<SearchResultItem> = payload.symbols
        .map { element -> element.split('|') }
        .map { SearchResultItem(it[0], it[1], it[2]) }

    class SearchViewModelFactory(
        private val argNetworkManager: NetworkManager,
        private val argPrefs: ApplicationPrefs
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(
                    NetworkManager::class.java,
                    ApplicationPrefs::class.java
                )
                .newInstance(
                    argNetworkManager,
                    argPrefs
                )
        }
    }
}
