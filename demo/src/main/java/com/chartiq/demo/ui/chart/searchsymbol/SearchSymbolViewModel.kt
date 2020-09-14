package com.chartiq.demo.ui.chart.searchsymbol

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chartiq.demo.ui.chart.searchsymbol.list.SearchResultItem
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class SearchSymbolViewModel : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    val resultLiveData: LiveData<List<SearchResultItem>>
        get() = mResultLiveData
    private val mResultLiveData = MutableLiveData<List<SearchResultItem>>()

    fun fetchSymbol(symbol: String) {
        launch {
            val client = OkHttpClient()
            val httpUrl = HttpUrl.Builder()
                .scheme(SCHEME)
                .host(HOST)
                .addPathSegment(PATH_SEARCH_SYMBOL)
                .addQueryParameter(PARAMETER_SYMBOL, symbol)
                .addQueryParameter(PARAMETER_MAX_RESULT, DEFAULT_VALUE_MAX_RESULT)
                .addQueryParameter(PARAMETER_FUND, DEFAULT_VALUE_FUNDS)
                .build()
            val request = Request.Builder()
                .url(httpUrl)
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) = Unit

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    val json = Gson().fromJson(responseBody, JsonObject::class.java)
                    val list = json.get("payload").asJsonObject.get("symbols").asJsonArray.toList()
                    mResultLiveData.postValue(list.mapElementToItem())
                }
            })
        }
    }

    private fun List<JsonElement>.mapElementToItem(): List<SearchResultItem> = this
        .map { element -> element.asString.split('|') }
        .map { SearchResultItem(it[0], it[1], it[2]) }

    companion object {
        private const val SCHEME = "https"
        private const val HOST = "symbols.chartiq.com"
        private const val PATH_SEARCH_SYMBOL = "chiq.symbolserver.SymbolLookup.service"

        private const val PARAMETER_SYMBOL = "t"
        private const val PARAMETER_MAX_RESULT = "x"
        private const val PARAMETER_FUND = "x"

        private const val DEFAULT_VALUE_MAX_RESULT = "50"
        private const val DEFAULT_VALUE_FUNDS =
            "[XNYS,XASE,XNAS,XASX,INDCBSX,INDXASE,INDXNAS,IND_DJI,ARCX,INDARCX,forex,mutualfund,futures]"
    }
}
