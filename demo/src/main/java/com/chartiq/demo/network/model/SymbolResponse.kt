package com.chartiq.demo.network.model

import com.google.gson.annotations.SerializedName

data class SymbolResponse(
    @SerializedName("mime_type")
    val mimeType: String,
    @SerializedName("payload")
    val payload: Payload,
    @SerializedName("message")
    val message: String
)

data class Payload(
    @SerializedName("symbols")
    val symbols: List<String>
)
