package com.chartiq.sdk;

import java.util.Map;

public interface DataSource {

    void pullInitialData(Map<String, Object> params, DataSourceCallback callback);

    void pullUpdateData(Map<String, Object> params, DataSourceCallback callback);

    void pullPaginationData(Map<String, Object> params, DataSourceCallback callback);
}
