package com.chartiq.demo.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.*
import com.chartiq.demo.databinding.FragmentChartBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.sdk.ChartIQView
import com.chartiq.sdk.DataSource
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.QuoteFeedParams

class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding

    private lateinit var chartIQView: ChartIQView

    private val mainViewModel: MainViewModel by activityViewModels()

    private val chartViewModel: ChartViewModel by viewModels(factoryProducer = {
        ChartViewModel.ChartViewModelFactory(
            ChartIQNetworkManager(), ApplicationPrefs.Default(requireContext())
        )
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)

        setupViews()
        initChartIQ()
        return binding.root
    }

    private fun initChartIQ() {
        chartIQView.apply {
            start(BuildConfig.DEFAULT_CHART_URL) {
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
                chartViewModel.fetchSavedSettings()
                mainViewModel.fetchData()
            }
        }
    }

    private fun setupViews() {
        mainViewModel.chartEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { command ->
                when (command) {
                    is ChartIQCommand.AddStudyList -> {
                        //todo identify when to pass true/false [Add Study case]
                       command.list.forEach {
                           chartIQView.addStudy(it, false)
                       }
                    }
                    ChartIQCommand.GetActiveStudies -> {
                        chartIQView.getActiveStudies { studyList ->
                            mainViewModel.activeStudies.postValue(studyList)
                        }
                    }
                    ChartIQCommand.GetAllStudies -> {
                        chartIQView.getStudyList { studyList ->
                            mainViewModel.allStudies.postValue(studyList)
                        }
                    }
                    is ChartIQCommand.DeleteStudy -> {
                        chartIQView.removeStudy(command.studyToDelete.shortName)
                    }
                }
            }
        }
        with(binding) {
            chartIQView = chartView

            symbolButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_searchSymbolFragment)
            }
            intervalButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_chooseIntervalFragment)
            }
            drawCheckBox.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_drawingToolFragment)
            }
        }

        chartViewModel.currentSymbol.observe(viewLifecycleOwner) { symbol ->
            binding.symbolButton.text = symbol.value
            chartIQView.setSymbol(symbol.value)
            chartIQView.setDataMethod(DataMethod.PULL, symbol.value)

        }
        chartViewModel.chartInterval.observe(viewLifecycleOwner) { chartInterval ->
            chartInterval.apply {
                binding.intervalButton.text = when (timeUnit) {
                    TimeUnit.SECOND,
                    TimeUnit.MINUTE -> {
                        "$duration${timeUnit.toString().first().toLowerCase()}"
                    }
                    else -> "$duration${timeUnit.toString().first()}"
                }
            }
        }

        chartViewModel.drawingTool.observe(viewLifecycleOwner) { drawingTool ->
            binding.drawCheckBox.isChecked = drawingTool != DrawingTool.NO_TOOL
        }
        chartViewModel.resultLiveData.observe(viewLifecycleOwner) { chartData ->
            chartIQView.post {
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

    private fun loadChartData(quoteFeedParams: QuoteFeedParams, callback: DataSourceCallback) {
        chartViewModel.getDataFeed(quoteFeedParams, callback)
    }
}
