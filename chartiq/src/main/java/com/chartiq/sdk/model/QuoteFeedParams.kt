package com.chartiq.sdk.model

/**
 * A data class for QuoteFeed parameters
 * @property symbol The symbol to fetch data for
 * @property period A period form the chart layout
 * @property interval A time unit from the chart layout
 * @property start A starting date for requesting data
 * @property end An end date for requesting data
 * @property meta Any object for meta parameter
 * @property callbackId callback id
 */
data class QuoteFeedParams(
    val symbol: String? = null,
    val period: Int? = null,
    val interval: String? = null,
    val start: String? = null,
    val end: String? = null,
    val meta: Any? = null,
    val callbackId: String? = null
)
