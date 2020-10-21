package com.chartiq.demo.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.BuildConfig
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChartBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.DataSource
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.QuoteFeedParams

class ChartFragment : Fragment() {

    private val chartIQHandler: ChartIQHandler by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }
    private lateinit var binding: FragmentChartBinding
    private val chartViewModel: ChartViewModel by viewModels(factoryProducer = {
        ChartViewModel.ChartViewModelFactory(ChartIQNetworkManager())
    })
    private val prefs by lazy {
        ApplicationPrefs.Default(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)

        setupViews()
        return binding.root
    }

    private fun setupViews() {
        val symbol = prefs.getChartSymbol().value
        chartIQHandler.apply {
            chartIQView = binding.chartIqView
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

        with(binding) {
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

            chartViewModel.resultLiveData.observe(viewLifecycleOwner) { chartData ->
                binding.chartIqView.post {
                    chartData.callback.execute(chartData.data)
                }
            }
            chartViewModel.errorLiveData.observe(viewLifecycleOwner) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.warning_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadChartData(quoteFeedParams: QuoteFeedParams, callback: DataSourceCallback) {
        chartViewModel.getDataFeed(quoteFeedParams, callback)
    }
}
