package com.chartiq.demo.network

import com.chartiq.demo.network.model.SymbolResponse
import com.chartiq.sdk.model.OHLCParams
import com.chartiq.sdk.model.QuoteFeedParams

interface NetworkManager {

    suspend fun fetchSymbol(symbol: String): NetworkResult<SymbolResponse>

    suspend fun fetchDataFeed(params: QuoteFeedParams): NetworkResult<List<OHLCParams>>
}
