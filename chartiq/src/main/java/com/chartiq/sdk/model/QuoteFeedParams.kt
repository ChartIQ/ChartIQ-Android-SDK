package com.chartiq.sdk.model

data class QuoteFeedParams(
    val symbol: String? = null,
    val period: Int? = null,
    val interval: String? = null,
    val start: String? = null,
    val end: String? = null,
    val meta: Any? = null,
    val callbackId: String? = null
)
