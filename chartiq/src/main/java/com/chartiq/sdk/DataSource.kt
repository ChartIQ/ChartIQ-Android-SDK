package com.chartiq.sdk

import java.util.Map

interface DataSource {

    fun pullInitialData(params: Map<String, Object>, callback: DataSourceCallback)

    fun pullUpdateData(params: Map<String, Object>, callback: DataSourceCallback)

    fun pullPaginationData(params: Map<String, Object>, callback: DataSourceCallback)
}
