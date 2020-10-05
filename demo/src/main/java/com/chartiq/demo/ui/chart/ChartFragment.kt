package com.chartiq.demo.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.BuildConfig
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChartBinding
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.sdk.DataSource
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.QuoteFeedParams

class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding
    private lateinit var chartViewModel: ChartViewModel
    private val prefs by lazy {
        ApplicationPrefs.Default(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        chartViewModel = ViewModelProvider(this).get(ChartViewModel::class.java)

        setupViews()
        return binding.root
    }

    private fun setupViews() = with(binding) {
        val symbol = prefs.getChartSymbol().value

        with(webview) {
            start(BuildConfig.DEFAULT_CHART_URL) {
                setDataMethod(DataMethod.PULL, symbol)
                setSymbol(symbol)
                setDataSource(object : DataSource {
                    override fun pullInitialData(
                        params: QuoteFeedParams,
                        callback: DataSourceCallback
                    ) {
                        loadChartData(params, callback)
                    }

                    override fun pullUpdateData(
                        params: QuoteFeedParams,
                        callback: DataSourceCallback
                    ) {
                        loadChartData(params, callback)
                    }

                    override fun pullPaginationData(
                        params: QuoteFeedParams,
                        callback: DataSourceCallback
                    ) {
                        loadChartData(params, callback)
                    }
                })
            }
        }
        symbolButton.apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_searchSymbolFragment)
            }
            text = symbol
        }
        intervalButton.apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_chooseIntervalFragment)
            }
            text = prefs.getChartInterval().run {
                when (timeUnit) {
                    TimeUnit.SECOND,
                    TimeUnit.MINUTE -> {
                        "$duration${timeUnit.toString().first().toLowerCase()}"
                    }
                    else -> "$duration${timeUnit.toString().first()}"
                }
            }
        }
        drawCheckBox.apply {
            isChecked = prefs.getDrawingTool() != DrawingTool.NO_TOOL
            setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_drawingToolFragment)
            }
        }
    }

    private fun loadChartData(quoteFeedParams: QuoteFeedParams, callback: DataSourceCallback) {
        chartViewModel.getDataFeed(quoteFeedParams) {
            binding.webview.post {
                callback.execute(it)
            }
        }
    }
}
