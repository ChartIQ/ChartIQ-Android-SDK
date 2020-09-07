package com.chartiq.demo.ui.chart;

import com.chartiq.sdk.model.OHLCParams;

interface Callback {
    void setOHLCChartData(OHLCParams[] data);
}
