package com.chartiq.sdk;

import com.chartiq.sdk.model.OHLCParams;

public interface DataSourceCallback {
    void execute(OHLCParams[] data);
}
