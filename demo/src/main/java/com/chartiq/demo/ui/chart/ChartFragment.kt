package com.chartiq.demo.ui.chart

import android.content.res.TypedArray
import android.graphics.Color
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
import com.chartiq.demo.ui.chart.drawingtools.DrawingToolFragment
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.demo.ui.chart.panel.PanelAdapter
import com.chartiq.demo.ui.chart.panel.color.ColorItem
import com.chartiq.demo.ui.chart.panel.color.ColorsAdapter
import com.chartiq.demo.ui.chart.panel.layer.ManageLayersModelBottomSheet
import com.chartiq.demo.ui.chart.panel.line.LineTypeAdapter
import com.chartiq.demo.ui.chart.panel.line.LineTypeItem
import com.chartiq.demo.ui.chart.panel.model.Instrument
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem
import com.chartiq.sdk.ChartIQView
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.DataSource
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.QuoteFeedParams
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.sdk.model.drawingtool.LineType
import kotlinx.coroutines.*

class ChartFragment : Fragment() {

    private val chartIQHandler: ChartIQHandler by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }
    private lateinit var binding: FragmentChartBinding
    private lateinit var panelAdapter: PanelAdapter
    private lateinit var panelList: List<InstrumentItem>

    private val chartViewModel: ChartViewModel by viewModels(factoryProducer = {
        ChartViewModel.ChartViewModelFactory(
            ChartIQNetworkManager(), ApplicationPrefs.Default(requireContext())
        )
    })
    private val mainViewModel by activityViewModels<MainViewModel>()

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
        chartIQHandler.apply {
            binding.chartIqView.apply {
                (chartIQView.parent as? FrameLayout)?.removeAllViews()
                addView(chartIQView)
            }
            start {
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
                mainViewModel.fetchActiveStudyData(chartIQHandler)
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
            crosshairCheckBox.setOnClickListener {
                crosshairLayout.root.apply {
                    visibility = if (visibility == View.GONE) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }
        }

            chartViewModel.currentSymbol.observe(viewLifecycleOwner) { symbol ->
                binding.symbolButton.text = symbol.value
                chartIQHandler.setSymbol(symbol.value)
                chartIQHandler.setDataMethod(DataMethod.PULL, symbol.value)

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
        chartViewModel.currentSymbol.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { symbol ->
                binding.symbolButton.text = symbol.value
                chartIQView.setSymbol(symbol.value)
                chartIQView.setDataMethod(DataMethod.PULL, symbol.value)
            }
        }
        chartViewModel.chartInterval.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { chartInterval ->
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
        chartViewModel.drawingTool.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { drawingTool ->
                with(binding) {
                    drawCheckBox.isChecked = drawingTool != DrawingTool.NO_TOOL
                    panelRecyclerView.visibility =
                        if (drawingTool != DrawingTool.NO_TOOL) {
                            setupPanel(drawingTool)
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    redoImageView.visibility = panelRecyclerView.visibility
                    undoImageView.visibility = panelRecyclerView.visibility
                }
            }
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
        chartViewModel.panelItemSelect.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { item ->
                val previousSelectedItem = panelAdapter.items.find { item.isSelected }
                panelAdapter.items = panelList.map {
                    it.copy(isSelected = it == item)
                }
                when (item.instrument) {
                    Instrument.DRAWING_TOOL ->
                        findNavController().navigate(R.id.action_mainFragment_to_drawingToolFragment)
                    Instrument.DELETE,
                    Instrument.CLONE -> {
                        refreshItemsWithDelay(REFRESH_LENGTH_MILLIS)
                        binding.instrumentRecyclerView.visibility = View.GONE
                    }
                    Instrument.FILL,
                    Instrument.COLOR -> binding.instrumentRecyclerView.apply {
                        visibility = if (item == previousSelectedItem) {
                            adapter = null
                            View.GONE
                        } else {
                            adapter = getColorsAdapter(resources.obtainTypedArray(R.array.colors))
                            View.VISIBLE
                        }
                    }
                    Instrument.LINE_TYPE -> binding.instrumentRecyclerView.apply {
                        visibility = if (item == previousSelectedItem) {
                            adapter = null
                            View.GONE
                        } else {
                            adapter = getLineTypeAdapter()
                            View.VISIBLE
                        }
                    }
                    Instrument.LAYER_MANAGEMENT -> {
                        ManageLayersModelBottomSheet().show(parentFragmentManager, null)
                        refreshItemsWithDelay(0L)
                    }
                    Instrument.SETTINGS -> refreshItemsWithDelay(REFRESH_LENGTH_MILLIS)
                }
            }
        }
    }

    private fun refreshItemsWithDelay(millis: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(millis)
            withContext(Dispatchers.Main) {
                panelAdapter.items = panelList.map { it.copy(isSelected = false) }
            }
        }
    }

    private fun setupPanel(drawingTool: DrawingTool) {
        with(binding) {
            // TODO: 29.10.20 Fetch drawing tool parameters
            panelAdapter = PanelAdapter(chartViewModel)
            val item = DrawingToolFragment.DEFAULT_TOOLS_LIST.find { it.tool == drawingTool }
            val list = chartViewModel.setupInstrumentsList(item!!)
            panelList = list

            panelAdapter.items = panelList
            panelRecyclerView.adapter = panelAdapter
        }
    }

    private fun loadChartData(
        quoteFeedParams: QuoteFeedParams,
        callback: DataSourceCallback
    ) {
        chartViewModel.getDataFeed(quoteFeedParams, callback)
    }

    private fun getColorsAdapter(colors: TypedArray): ColorsAdapter {
        val colorsList = mutableListOf<ColorItem>()
        for (index in 0 until colors.length()) {
            colorsList.add(ColorItem(colors.getColor(index, Color.WHITE)))
        }
        colors.recycle()
        return ColorsAdapter().apply { items = colorsList }
    }

    private fun getLineTypeAdapter(): LineTypeAdapter =
        LineTypeAdapter().apply { items = DEFAULT_LINE_TYPE_LIST }

    companion object {
        private const val REFRESH_LENGTH_MILLIS = 400L

        private val DEFAULT_LINE_TYPE_LIST = listOf(
            LineTypeItem(LineType.SOLID, R.drawable.ic_line_type_solid),
            LineTypeItem(LineType.SOLID, R.drawable.ic_line_type_solid_bold),
            LineTypeItem(LineType.SOLID, R.drawable.ic_line_type_solid_boldest),
            LineTypeItem(LineType.DOTTED, R.drawable.ic_line_type_dotted),
            LineTypeItem(LineType.DOTTED, R.drawable.ic_line_type_dotted_bold),
            LineTypeItem(LineType.DASHED, R.drawable.ic_line_type_dash),
            LineTypeItem(LineType.DASHED, R.drawable.ic_line_type_dash_bold)
        )
    }
}
