package com.chartiq.demo.network.api

import com.chartiq.demo.network.model.SymbolResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SymbolsAPI {

    @GET("chiq.symbolserver.SymbolLookup.service")
    fun fetchSymbolAsync(
        @Query("t") symbol: String,
        @Query("x") maxResult: String,
        @Query("x") fund: String
    ): Deferred<Response<SymbolResponse>>
}
