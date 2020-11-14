package com.chartiq.demo.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChartBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.DrawingTool

class ChartFragment : Fragment() {

    private val chartIQHandler: ChartIQHandler by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }
    private lateinit var binding: FragmentChartBinding

    private val mainViewModel: MainViewModel by viewModels(factoryProducer = {
        MainViewModel.ViewModelFactory(
            ChartIQNetworkManager(),
            ApplicationPrefs.Default(requireContext()),
            chartIQHandler
        )
    })

    private val chartViewModel: ChartViewModel by viewModels(factoryProducer = {
        ChartViewModel.ChartViewModelFactory(
            ApplicationPrefs.Default(requireContext()),
            ChartIQNetworkManager(),
        )
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        setupViews()
        setChartIQView()
        return binding.root
    }

    private fun setChartIQView() {
        chartIQHandler.apply {
            binding.chartIqView.apply {
                (chartIQView.parent as? FrameLayout)?.removeAllViews()
                addView(chartIQView)
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

            chartViewModel.drawingTool.observe(viewLifecycleOwner) { drawingTool ->
                binding.drawCheckBox.isChecked = drawingTool != DrawingTool.NO_TOOL
            }
            mainViewModel.errorLiveData.observe(viewLifecycleOwner) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.warning_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
