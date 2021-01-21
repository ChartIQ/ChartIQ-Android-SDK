package com.chartiq.demo.network

import android.annotation.SuppressLint
import com.chartiq.demo.network.api.ChartAPI
import com.chartiq.demo.network.api.SymbolsAPI
import com.chartiq.demo.network.model.SymbolResponse
import com.chartiq.sdk.model.OHLCParams
import com.chartiq.sdk.model.QuoteFeedParams
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ChartIQNetworkManager : NetworkManager {

    private val chartRetrofit by lazy {
        getRetrofit(HOST_SIMULATOR, ChartAPI::class.java)
    }
    private val symbolsRetrofit by lazy {
        getRetrofit(HOST_SYMBOLS, SymbolsAPI::class.java)
    }

    @SuppressLint("HardwareIds")
    override suspend fun fetchDataFeed(
        params: QuoteFeedParams,
        applicationId: String
    ): NetworkResult<List<OHLCParams>> {
        return try {
            chartRetrofit
                .fetchDataFeedAsync(
                    params.symbol,
                    params.start,
                    params.interval,
                    params.period?.toString(),
                    DEFAULT_VALUE_EXTENDED,
                    applicationId
                )
                .safeExtractNetworkResult()
        } catch (e: UnknownHostException) {
            // The exception may occurs when the device has no internet connection
            NetworkResult.Failure(NetworkException(null, 3000))
        } catch (e: SocketTimeoutException) {
            // The exception may occurs when the internet connection is not stable
            NetworkResult.Failure(NetworkException(null, 3001))
        }
    }

    override suspend fun fetchSymbol(
        symbol: String,
        filter: String?
    ): NetworkResult<SymbolResponse> {
        return symbolsRetrofit
            .fetchSymbolAsync(
                symbol,
                DEFAULT_VALUE_MAX_RESULT,
                DEFAULT_VALUE_FUNDS,
                filter
            )
            .safeExtractNetworkResult()
    }

    private fun <T> getRetrofit(baseUrl: String, api: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

    companion object {
        private const val HOST_SIMULATOR = "https://simulator.chartiq.com"
        private const val HOST_SYMBOLS = "https://symbols.chartiq.com"

        private const val DEFAULT_VALUE_EXTENDED = "1"

        private const val DEFAULT_VALUE_MAX_RESULT = "50"
        private const val DEFAULT_VALUE_FUNDS =
            "[XNYS,XASE,XNAS,XASX,INDCBSX,INDXASE,INDXNAS,IND_DJI,ARCX,INDARCX,forex,mutualfund,futures]"
    }
}
