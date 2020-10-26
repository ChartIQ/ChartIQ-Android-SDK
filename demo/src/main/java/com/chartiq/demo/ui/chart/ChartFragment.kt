package com.chartiq.demo.ui.chart

import android.content.res.TypedArray
import android.graphics.Color
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
import com.chartiq.demo.ui.chart.drawingtools.DrawingToolFragment
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.demo.ui.chart.panel.Instrument
import com.chartiq.demo.ui.chart.panel.PanelAdapter
import com.chartiq.demo.ui.chart.panel.PanelItem
import com.chartiq.demo.ui.chart.panel.color.ColorItem
import com.chartiq.demo.ui.chart.panel.color.ColorsAdapter
import com.chartiq.demo.ui.chart.panel.layer.ManageLayersModelBottomSheet
import com.chartiq.demo.ui.chart.panel.line.LineTypeAdapter
import com.chartiq.demo.ui.chart.panel.line.LineTypeItem
import com.chartiq.sdk.ChartIQView
import com.chartiq.sdk.DataSource
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.LineType
import com.chartiq.sdk.model.QuoteFeedParams
import kotlinx.coroutines.*

class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding
    private lateinit var chartIQView: ChartIQView
    private lateinit var panelAdapter: PanelAdapter
    private lateinit var panelList: List<PanelItem>
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
            }
        }
    }

    private fun setupViews() {
        mainViewModel.chartEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { command ->
                when (command) {
                    is ChartIQCommand.AddStudy -> {
                        //todo identify when to pass true/false [Add Study case]
                        chartIQView.addStudy(command.study, true)
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

        chartViewModel.drawingTool.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { drawingTool ->
                binding.drawCheckBox.isChecked = drawingTool != DrawingTool.NO_TOOL
                binding.panelRecyclerView.visibility =
                    if (drawingTool != DrawingTool.NO_TOOL) {
                        setupPanel(drawingTool)
                        View.VISIBLE
                    } else {
                        View.GONE
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
                val previousSelectedItem = panelAdapter.items.find { it.isSelected }
                panelAdapter.items = panelList.map {
                    it.copy(isSelected = it == item)
                }
                when (item.instrument) {
                    Instrument.DRAWING_TOOL ->
                        findNavController().navigate(R.id.action_mainFragment_to_drawingToolFragment)
                    Instrument.DELETE,
                    Instrument.CLONE -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(400L)
                            withContext(Dispatchers.Main) {
                                panelAdapter.items = panelList.map { it.copy(isSelected = false) }
                            }
                        }
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
                    Instrument.LAYER_MANAGEMENT ->
                        ManageLayersModelBottomSheet().show(parentFragmentManager, null)
                    Instrument.OPTIONS -> Unit
//                                findNavController().navigate(R.id.action_mainFragment_to_drawingToolSettingsFragment)
                }
            }
        }
    }

    private fun setupPanel(drawingTool: DrawingTool) {
        with(binding) {
            panelAdapter = PanelAdapter(chartViewModel)
            // TODO: 22.10.20 Refactor
            val item = DrawingToolFragment.DEFAULT_TOOLS_LIST.find { it.tool == drawingTool }
            val list = DEFAULT_INSTRUMENTS_LIST
                .toMutableList()
            list.add(0, PanelItem(Instrument.DRAWING_TOOL, item!!.iconRes))
            panelList = list

            panelAdapter.items = panelList
            panelRecyclerView.adapter = panelAdapter
        }
    }

    private fun loadChartData(quoteFeedParams: QuoteFeedParams, callback: DataSourceCallback) {
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
        private val DEFAULT_INSTRUMENTS_LIST = listOf(
            PanelItem(Instrument.FILL, R.drawable.ic_panel_fill),
            PanelItem(Instrument.COLOR, R.drawable.ic_panel_color),
            PanelItem(Instrument.LINE_TYPE, R.drawable.ic_panel_line_type),
            PanelItem(Instrument.CLONE, R.drawable.ic_panel_clone),
            PanelItem(Instrument.DELETE, R.drawable.ic_panel_delete),
            PanelItem(Instrument.LAYER_MANAGEMENT, R.drawable.ic_panel_layers_management),
            PanelItem(Instrument.OPTIONS, R.drawable.ic_panel_options)
        )

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
