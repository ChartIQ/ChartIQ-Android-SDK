package com.chartiq.demo.network.api

import com.chartiq.demo.network.model.SymbolResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SymbolsAPI {

    @GET("chiq.symbolserver.SymbolLookup.service")
    suspend fun fetchSymbolAsync(
        @Query("t") symbol: String,
        @Query("m") maxResult: String,
        @Query("x") fund: String,
        @Query("e") filter: String?
    ): Response<SymbolResponse>
}
