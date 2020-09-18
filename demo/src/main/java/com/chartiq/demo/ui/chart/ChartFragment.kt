package com.chartiq.demo.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.chartiq.demo.R
import com.chartiq.sdk.ChartIQWebView
import com.chartiq.sdk.DataMethod
import com.chartiq.sdk.DefaultChartIQ

class ChartFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chartViewModel =
            ViewModelProviders.of(this).get(ChartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_chart, container, false)
        val chartIQ: ChartIQWebView = root.findViewById<WebView>(R.id.webview) as ChartIQWebView
        setup(chartIQ)
        return root
    }

    private fun setup(chartIQ: ChartIQWebView) {
        chartIQ.start(CHART_URL) {
            chartIQ.setDataMethod(DataMethod.PULL, DEFAULT_SYMBOL);
            chartIQ.setSymbol(DEFAULT_SYMBOL);
        }
    }

    companion object {
        private const val DEFAULT_SYMBOL = "AAPL"
        private const val CHART_URL = "https://i.codeit.pro/autobuild-bot/i/Android/ChartIQ/ChartIQJS/helloworld.html";
    }
}
