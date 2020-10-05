package com.chartiq.demo.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartiq.sdk.model.OHLCParams
import com.chartiq.sdk.model.QuoteFeedParams
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.Callback
import java.io.IOException

class ChartViewModel : ViewModel() {
    private val client = OkHttpClient()

    // TODO: 05.10.20 Refactor the following method
    // TODO: 02.10.20 Check what should be used: session or seed as the last query parameter
    fun getDataFeed(params: QuoteFeedParams, callback: (list: List<OHLCParams>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val httpUrl = HttpUrl.Builder()
                .scheme(SCHEME)
                .host(HOST)
                .addPathSegment(PATH_DATA_FEED)
                .addQueryParameter(PARAMETER_IDENTIFIER, params.symbol)
                .addQueryParameter(PARAMETER_START_DATE, params.start)
                .addQueryParameter(PARAMETER_INTERVAL, params.interval)
                .addQueryParameter(PARAMETER_PERIOD, params.period?.toString())
                .addQueryParameter(PARAMETER_EXTENDED, DEFAULT_VALUE_EXTENDED)
                .addQueryParameter(PARAMETER_SESSION, params.callbackId)
                .build()
            val request = Request.Builder()
                .url(httpUrl)
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                // TODO: 05.10.20 Handle onFailure
                override fun onFailure(call: Call, e: IOException) = Unit

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    val json = Gson().fromJson(responseBody, Array<OHLCParams>::class.java)
                    callback(json.toList())
                }
            })
        }
    }

    companion object {
        private const val SCHEME = "https"
        private const val HOST = "simulator.chartiq.com"
        private const val PATH_DATA_FEED = "datafeed"

        private const val PARAMETER_IDENTIFIER = "identifier"
        private const val PARAMETER_START_DATE = "startdate"
        private const val PARAMETER_INTERVAL = "interval"
        private const val PARAMETER_PERIOD = "period"
        private const val PARAMETER_EXTENDED = "extended"
        private const val PARAMETER_SESSION = "session"

        private const val DEFAULT_VALUE_EXTENDED = "1"
    }
}
