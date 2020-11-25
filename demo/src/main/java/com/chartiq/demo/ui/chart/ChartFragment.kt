package com.chartiq.demo.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isVisible
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
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.chart.panel.InstrumentPanelAdapter
import com.chartiq.demo.ui.chart.panel.layer.ManageLayersModelBottomSheet
import com.chartiq.demo.ui.chart.panel.model.Instrument
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem
import com.chartiq.demo.ui.chart.panel.settings.DrawingToolSettingsFragmentArgs
import com.chartiq.demo.ui.common.colorpicker.ColorItem
import com.chartiq.demo.ui.common.colorpicker.ColorsAdapter
import com.chartiq.demo.ui.common.colorpicker.findColorIndex
import com.chartiq.demo.ui.common.linepicker.LineAdapter
import com.chartiq.demo.ui.common.linepicker.LineItem
import com.chartiq.demo.ui.common.linepicker.findLineIndex
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.DataSource
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.ChartLayer
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.QuoteFeedParams
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.demo.network.model.PanelDrawingToolParameters
import com.chartiq.sdk.model.drawingtool.LineType
import com.chartiq.sdk.model.drawingtool.drawingmanager.ChartIQDrawingManager
import kotlinx.coroutines.*

class ChartFragment : Fragment(), ManageLayersModelBottomSheet.DialogFragmentListener {

    private val chartIQHandler: ChartIQHandler by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }
    private lateinit var binding: FragmentChartBinding
    private lateinit var panelAdapter: InstrumentPanelAdapter
    private lateinit var panelList: List<InstrumentItem>
    private val job = SupervisorJob()
    private val chartScope = CoroutineScope(job + Dispatchers.IO)
    private var drawingToolParameters: PanelDrawingToolParameters? = null

    private val chartViewModel: ChartViewModel by viewModels(factoryProducer = {
        ChartViewModel.ChartViewModelFactory(
            ChartIQNetworkManager(),
            ApplicationPrefs.Default(requireContext()),
            chartIQHandler,
            ChartIQDrawingManager()
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
            with(binding.chartIqView) {
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

    override fun onResume() {
        super.onResume()
        chartViewModel.fetchSavedSettings()
    }

    override fun onPause() {
        super.onPause()
        job.cancelChildren()
    }

    override fun onManageLayer(layer: ChartLayer) {
        chartViewModel.manageLayer(layer)
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
                        enableCrosshair()
                        View.VISIBLE
                    } else {
                        disableCrosshair()
                        View.GONE
                    }
                }
            }
        }
        with(chartViewModel) {
            resultLiveData.observe(viewLifecycleOwner) { chartData ->
                binding.chartIqView.post {
                    chartData.callback.execute(chartData.data)
                }
            }
            currentSymbol.observe(viewLifecycleOwner) { symbol ->
                binding.symbolButton.text = symbol.value
                chartViewModel.setSymbol(symbol)
                chartViewModel.setDataMethod(DataMethod.PULL, symbol)
            }
            chartInterval.observe(viewLifecycleOwner) { chartInterval ->
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

            drawingTool.observe(viewLifecycleOwner) { drawingTool ->
                val isDrawingToolSelected = drawingTool != DrawingTool.NONE
                if (isDrawingToolSelected) {
                    chartViewModel.enableDrawing(drawingTool)
                    binding.redoImageView.setOnClickListener {
                        chartViewModel.redoDrawingChange()
                    }
                    binding.undoImageView.setOnClickListener {
                        chartViewModel.undoDrawingChange()
                    }
                    panelAdapter = InstrumentPanelAdapter()
                }

                with(binding) {
                    drawCheckBox.isChecked = isDrawingToolSelected
                    panelRecyclerView.isVisible = isDrawingToolSelected
                    redoImageView.isVisible = isDrawingToolSelected
                    undoImageView.isVisible = isDrawingToolSelected
                }
            }
            parameters.observe(viewLifecycleOwner) { parameters ->
                drawingToolParameters = parameters
                panelList = chartViewModel.setupInstrumentsList()

                panelAdapter.parameters = parameters
                panelAdapter.items = panelList
                panelAdapter.listener = OnSelectItemListener {
                    onSelectItem(it)
                }

                binding.panelRecyclerView.adapter = panelAdapter
            }
            crosshairHUD.observe(viewLifecycleOwner) { hud ->
                with(binding.crosshairLayout) {
                    priceValueTextView.text = hud.price
                    volValueTextView.text = hud.volume
                    openValueTextView.text = hud.open
                    highValueTextView.text = hud.high
                    closeValueTextView.text = hud.close
                    lowValueTextView.text = hud.low
                }
            }

            resetInstrumentsLiveData.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let{
                    panelAdapter.items = panelList.map { it.copy(isSelected = false) }
                }
            }

            errorLiveData.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.warning_something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun enableCrosshair() {
        chartViewModel.enableCrosshairs()
        chartScope.launch {
            while (true) {
                delay(CROSSHAIR_UPDATE_PERIOD)
                withContext(Dispatchers.Main) {
                    chartViewModel.getHUDDetails()
                }
            }
        }
    }

    private fun disableCrosshair() {
        chartViewModel.disableCrosshairs()
        job.cancelChildren()
    }

    private fun onSelectItem(item: InstrumentItem) {
        val previousItem = panelList.find { it.isSelected }
        if (item.instrument == previousItem?.instrument && binding.instrumentRecyclerView.isVisible) {
            binding.instrumentRecyclerView.isVisible = false
            chartViewModel.resetInstruments()
            return
        }

        panelList = panelList.map {
            it.copy(isSelected = it.instrument == item.instrument)
        }
        panelAdapter.items = panelList

        when (item.instrument) {
            Instrument.DRAWING_TOOL -> navigateToDrawingTools()
            Instrument.FILL -> showFillColorCarousel()
            Instrument.COLOR -> showColorCarousel()
            Instrument.LINE_TYPE -> showLineTypeCarousel()
            Instrument.CLONE -> cloneDrawing()
            Instrument.DELETE -> deleteDrawing()
            Instrument.MAGNET -> chartViewModel.magnetDrawing()
            Instrument.LAYER_MANAGEMENT -> showLayerManagementDialogue()
            Instrument.SETTINGS -> navigateToInstrumentSettings()
        }
    }

    private fun loadChartData(
        quoteFeedParams: QuoteFeedParams,
        callback: DataSourceCallback
    ) {
        chartViewModel.getDataFeed(quoteFeedParams, callback)
    }

    private fun navigateToDrawingTools() {
        findNavController().navigate(R.id.action_mainFragment_to_drawingToolFragment)
    }

    private fun navigateToInstrumentSettings() {
        findNavController().navigate(
            R.id.action_mainFragment_to_drawingToolSettingsFragment,
            DrawingToolSettingsFragmentArgs(chartViewModel.drawingTool.value!!).toBundle()
        )
    }

    private fun deleteDrawing() {
        chartViewModel.deleteDrawing()
    }

    private fun cloneDrawing() {
        chartViewModel.cloneDrawing()
    }

    private fun showFillColorCarousel() {
        val colors = getColors()
        val colorsAdapter = ColorsAdapter()
        // If `fillColor` color is in colors list then it should be selected
        val scrollIndex = findColorIndex(colors, drawingToolParameters?.fillColor)
        colorsAdapter.items = colors
            .mapIndexed { index, it -> it.copy(isSelected = index == scrollIndex) }
        colorsAdapter.listener = OnSelectItemListener { colorItem ->
            chartViewModel.updateFillColor(colorItem.color)
            binding.instrumentRecyclerView.isVisible = false
        }

        binding.instrumentRecyclerView.apply {
            setHasFixedSize(true)
            adapter = colorsAdapter
            isVisible = true
            post {
                scrollIndex?.let { smoothScrollToPosition(it) }
            }
        }
    }

    private fun showColorCarousel() {
        val colors = getColors()
        val colorsAdapter = ColorsAdapter()
        // If `color` color is in colors list then it should be selected
        val scrollIndex = findColorIndex(colors, drawingToolParameters?.color)
        colorsAdapter.items = colors
            .mapIndexed { index, it -> it.copy(isSelected = index == scrollIndex) }
        colorsAdapter.listener = OnSelectItemListener { colorItem ->
            chartViewModel.updateColor(colorItem.color)
            binding.instrumentRecyclerView.isVisible = false
        }
        binding.instrumentRecyclerView.apply {
            setHasFixedSize(true)
            adapter = colorsAdapter
            isVisible = true
            post {
                scrollIndex?.let { smoothScrollToPosition(it) }
            }
        }
    }

    private fun showLineTypeCarousel() {
        val lines = DEFAULT_LINE_TYPE_LIST
        val linesAdapter = LineAdapter()

        val scrollIndex = findLineIndex(
            lines,
            drawingToolParameters?.lineType,
            drawingToolParameters?.lineWidth
        )
        linesAdapter.items = lines
            .mapIndexed { index, it -> it.copy(isSelected = index == scrollIndex) }
        linesAdapter.listener = OnSelectItemListener { lineItem ->
            chartViewModel.updateLine(lineItem.lineType, lineItem.lineWidth)
            binding.instrumentRecyclerView.isVisible = false
        }

        binding.instrumentRecyclerView.apply {
            setHasFixedSize(true)
            adapter = linesAdapter
            isVisible = true
            post {
                scrollIndex?.let { smoothScrollToPosition(it) }
            }
        }
    }

    private fun showLayerManagementDialogue() {
        val dialogue = ManageLayersModelBottomSheet()
        dialogue.setTargetFragment(this, REQUEST_CODE_MANAGE_LAYERS)
        dialogue.show(parentFragmentManager, null)
    }

    private fun getColors(): List<ColorItem> {
        val colors = resources.obtainTypedArray(R.array.colors)
        val colorsList = mutableListOf<ColorItem>()
        for (index in 0 until colors.length()) {
            colorsList.add(ColorItem(colors.getColor(index, Color.WHITE)))
        }
        colors.recycle()
        return colorsList
    }

    companion object {
        private const val CROSSHAIR_UPDATE_PERIOD = 300L

        private const val REQUEST_CODE_MANAGE_LAYERS = 1012

        val DEFAULT_LINE_TYPE_LIST = listOf(
            LineItem(LineType.SOLID, 1, R.drawable.ic_line_type_solid),
            LineItem(LineType.SOLID, 2, R.drawable.ic_line_type_solid_bold),
            LineItem(LineType.SOLID, 3, R.drawable.ic_line_type_solid_boldest),
            LineItem(LineType.DOTTED, 1, R.drawable.ic_line_type_dotted),
            LineItem(LineType.DOTTED, 2, R.drawable.ic_line_type_dotted_bold),
            LineItem(LineType.DASHED, 1, R.drawable.ic_line_type_dash),
            LineItem(LineType.DASHED, 2, R.drawable.ic_line_type_dash_bold)
        )
    }
}
