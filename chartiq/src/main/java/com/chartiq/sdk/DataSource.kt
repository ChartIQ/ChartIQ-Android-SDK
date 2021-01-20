package com.chartiq.sdk

import com.chartiq.sdk.model.QuoteFeedParams

interface DataSource {

    fun pullInitialData(params: QuoteFeedParams, callback: DataSourceCallback)

    fun pullUpdateData(params: QuoteFeedParams, callback: DataSourceCallback)

    fun pullPaginationData(params: QuoteFeedParams, callback: DataSourceCallback)
}
