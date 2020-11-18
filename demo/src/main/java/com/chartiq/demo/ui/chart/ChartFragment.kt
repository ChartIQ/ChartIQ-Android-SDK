package com.chartiq.demo.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChartBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.DataSource
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.QuoteFeedParams

class ChartFragment : Fragment() {

    private val chartIQ: ChartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private lateinit var binding: FragmentChartBinding

    private val chartViewModel: ChartViewModel by viewModels(factoryProducer = {
        ChartViewModel.ChartViewModelFactory(
            ChartIQNetworkManager(), ApplicationPrefs.Default(requireContext())
        )
    })
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        setupViews()
        initChartIQ()
        return binding.root
    }

    private fun initChartIQ() {
        chartIQ.apply {
            binding.chartIqView.apply {
                (chartView.parent as? FrameLayout)?.removeAllViews()
                addView(chartView)
            }
            start {
                setDataSource(object : DataSource {
                    override fun pullInitialData(
                        params: QuoteFeedParams,
                        callback: DataSourceCallback,
                    ) {
                        loadChartData(params, callback)
                    }

                    override fun pullUpdateData(
                        params: QuoteFeedParams,
                        callback: DataSourceCallback,
                    ) {
                        loadChartData(params, callback)
                    }

                    override fun pullPaginationData(
                        params: QuoteFeedParams,
                        callback: DataSourceCallback,
                    ) {
                        loadChartData(params, callback)
                    }
                })
                chartViewModel.fetchSavedSettings()
                mainViewModel.fetchActiveStudyData(chartIQ)
            }
        }
    }

    private fun setupViews() {
        with(binding) {
            symbolButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_searchSymbolFragment)
            }
            intervalButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_chooseIntervalFragment)
            }
            drawCheckBox.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_drawingToolFragment)
            }

            chartViewModel.currentSymbol.observe(viewLifecycleOwner) { symbol ->
                binding.symbolButton.text = symbol.value
                chartIQ.setSymbol(symbol.value)
                chartIQ.setDataMethod(DataMethod.PULL, symbol.value)

            }
            chartViewModel.chartInterval.observe(viewLifecycleOwner) { chartInterval ->
                chartInterval.apply {
                    binding.intervalButton.text = when (timeUnit) {
                        TimeUnit.SECOND,
                        TimeUnit.MINUTE,
                        -> {
                            "$duration${timeUnit.toString().first().toLowerCase()}"
                        }
                        else -> "$duration${timeUnit.toString().first()}"
                    }
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
            chartViewModel.drawingTool.observe(viewLifecycleOwner) { drawingTool ->
                binding.drawCheckBox.isChecked = drawingTool != DrawingTool.NO_TOOL
            }
            chartViewModel.resultLiveData.observe(viewLifecycleOwner) { chartData ->
                binding.chartIqView.post {
                    chartData.callback.execute(chartData.data)
                }
            }
        }
    }

    private fun loadChartData(
        quoteFeedParams: QuoteFeedParams,
        callback: DataSourceCallback,
    ) {
        chartViewModel.getDataFeed(quoteFeedParams, callback)
    }
}
