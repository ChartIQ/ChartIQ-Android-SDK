package com.chartiq.sdk;

import com.chartiq.sdk.model.OHLCChart;

public interface DataSourceCallback {
    void execute(OHLCChart[] data);
}
