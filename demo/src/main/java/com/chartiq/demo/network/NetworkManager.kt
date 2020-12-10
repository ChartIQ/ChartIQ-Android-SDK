package com.chartiq.demo.network

import com.chartiq.demo.network.model.SymbolResponse
import com.chartiq.sdk.model.OHLCParams
import com.chartiq.sdk.model.QuoteFeedParams

interface NetworkManager {

    suspend fun fetchSymbol(symbol: String, filter: String? = null): NetworkResult<SymbolResponse>

    suspend fun fetchDataFeed(params: QuoteFeedParams, applicationId: String): NetworkResult<List<OHLCParams>>
}
