package com.chartiq.demo.network.api

import com.chartiq.sdk.model.OHLCParams
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ChartAPI {

    @GET("datafeed")
    fun fetchDataFeedAsync(
        @Query("identifier") identifier: String?,
        @Query("startdate") startDate: String?,
        @Query("interval") interval: String?,
        @Query("period") period: String?,
        @Query("extended") extended: String?,
        @Query("session") session: String?,
    ): Deferred<Response<List<OHLCParams>>>
}
