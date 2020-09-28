package com.chartiq.demo.ui.chart

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.BuildConfig
import com.chartiq.demo.R
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.sdk.ChartIQView
import com.chartiq.sdk.DataSource
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.OnStartCallback
import com.chartiq.sdk.model.DataMethod
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Map

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

        setupUI(root)
        return root
    }

    private fun setupUI(root: View) {
        val chartIQ: ChartIQView = root.findViewById<WebView>(R.id.webview) as ChartIQView
        val prefs = ApplicationPrefs.Default(requireContext())
        val symbol = prefs.getChartSymbol().value

        chartIQ.start(BuildConfig.DEFAULT_CHART_URL, object : OnStartCallback {
            override fun onStart() {
                chartIQ.setDataMethod(DataMethod.PULL, symbol);
                chartIQ.setSymbol(symbol);
                chartIQ.setDataSource(object : DataSource {
                    override fun pullInitialData(
                        params: Map<String, Object>,
                        callback: DataSourceCallback
                    ) {
                        loadChartData(params, callback)
                    }

                    override fun pullUpdateData(
                        params: Map<String, Object>,
                        callback: DataSourceCallback
                    ) {
                        loadChartData(params, callback)
                    }

                    override fun pullPaginationData(
                        params: Map<String, Object>,
                        callback: DataSourceCallback
                    ) {
                        loadChartData(params, callback)
                    }
                })
            }
        })

        root.findViewById<Button>(R.id.symbolButton).apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_searchSymbolFragment)
            }
            text = symbol
        }
        root.findViewById<Button>(R.id.intervalButton).apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_chooseIntervalFragment)
            }
            text = ApplicationPrefs.Default(requireContext()).getChartInterval().run {
                when (timeUnit) {
                    TimeUnit.SECOND,
                    TimeUnit.MINUTE -> {
                        "$value${timeUnit.toString().first().toLowerCase()}"
                    }
                    else -> "$value${timeUnit.toString().first()}"
                }
            }
        }
    }

    // TODO: 07.09.20 Refactor the following implementation
    private fun loadChartData(params: Map<String, Object>, callback: DataSourceCallback?) {
        if (!params.containsKey("start") || params["start"] == null || params["start"]!!.equals("")) {
            params.put("start", "2016-12-16T16:00:00.000Z" as Object)
        }
        if (params.containsKey("end") || params["start"]!!.equals("")) {
            val tz = TimeZone.getTimeZone("UTC")
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
            df.timeZone = tz
            val endDate = df.format(Date())
            params.put("end", endDate as Object)
        }


        val isMinute =
            params.containsKey("interval") && TextUtils.isDigitsOnly(
                params["interval"].toString()
            )
        params.put("interval", if (isMinute) "minute" as Object else params["interval"]!!)
        params.put("period", if (isMinute) "minute" as Object else params["period"]!!)

        val builder = StringBuilder()
        builder.append("http://simulator.chartiq.com/datafeed?")
        builder.append("identifier=" + params["symbol"])
        builder.append("&startdate=" + params["start"])
        if (params.containsKey("end")) {
            builder.append("&enddate=" + params["end"])
        }
        builder.append("&interval=" + params["interval"])
        builder.append("&period=" + params["period"])
        builder.append("&seed=1001")
        val url = builder.toString()
        val symbol = params["symbol"].toString()
        TemproraryAsyncTask(url, Callback { data ->
            data?.let {
                callback!!.execute(it)
            }
        }).execute()
    }
}
