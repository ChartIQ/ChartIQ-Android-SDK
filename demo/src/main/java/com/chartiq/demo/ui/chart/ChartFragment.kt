package com.chartiq.demo.ui.chart

import android.animation.LayoutTransition
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChartBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.network.model.PanelDrawingToolParameters
import com.chartiq.demo.ui.MainFragmentDirections
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.chart.fullview.CollapseButtonOnSwipeTouchListener
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.demo.ui.chart.panel.InstrumentPanelAdapter
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.chart.panel.layer.ManageLayersModelBottomSheet
import com.chartiq.demo.ui.chart.panel.model.Instrument
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem
import com.chartiq.demo.ui.common.colorpicker.ColorItem
import com.chartiq.demo.ui.common.colorpicker.ColorsAdapter
import com.chartiq.demo.ui.common.colorpicker.convertStringColorToInt
import com.chartiq.demo.ui.common.colorpicker.findColorIndex
import com.chartiq.demo.ui.common.linepicker.LineAdapter
import com.chartiq.demo.ui.common.linepicker.LineItem
import com.chartiq.demo.ui.common.linepicker.findLineIndex
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.ChartLayer
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.sdk.model.drawingtool.drawingmanager.ChartIQDrawingManager

class ChartFragment : Fragment(), ManageLayersModelBottomSheet.DialogFragmentListener {

    private val chartIQ: ChartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private lateinit var binding: FragmentChartBinding
    private lateinit var panelList: List<InstrumentItem>
    private var drawingToolParameters: PanelDrawingToolParameters? = null
    private val colorsAdapter by lazy { ColorsAdapter() }
    private val linesAdapter by lazy { LineAdapter() }
    private val panelAdapter: InstrumentPanelAdapter by lazy { InstrumentPanelAdapter() }
    private val collapseFullviewButtonOnSwipeListener by lazy {
        CollapseButtonOnSwipeTouchListener(binding.root, requireContext())
    }

    private val mainViewModel: MainViewModel by activityViewModels(factoryProducer = {
        MainViewModel.ViewModelFactory(
            ChartIQNetworkManager(),
            ApplicationPrefs.Default(requireContext()),
            chartIQ
        )
    })

    private val chartViewModel: ChartViewModel by viewModels(factoryProducer = {
        ChartViewModel.ChartViewModelFactory(
            ChartIQNetworkManager(),
            ApplicationPrefs.Default(requireContext()),
            chartIQ,
            ChartIQDrawingManager()
        )
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        setupViews()
        setChartIQView()
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        chartViewModel.toggleFullscreen()
    }

    private fun setChartIQView() {
        chartIQ.chartView.apply {
            (parent as? FrameLayout)?.removeAllViews()
            binding.chartIqView.addView(this)
        }
        mainViewModel.setupChart()
    }

    override fun onResume() {
        super.onResume()
        chartViewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        chartViewModel.onPause()
    }

    override fun onManageLayer(layer: ChartLayer) {
        chartViewModel.manageLayer(layer)
    }

    private fun setupViews() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            chartViewModel.toggleFullscreen()
        }
        with(binding) {
            root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            symbolButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_searchSymbolFragment)
            }
            intervalButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_chooseIntervalFragment)
            }
            drawCheckBox.setOnClickListener {
                chartViewModel.toggleDrawingTool()
            }
            crosshairCheckBox.setOnClickListener {
                chartViewModel.toggleCrosshairs()
            }
            fullviewCheckBox.setOnClickListener {
                toggleFullscreenViews(true)
            }
            collapseFullviewCheckBox.setOnClickListener {
                toggleFullscreenViews(false)
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
                with(binding) {
                    if (isDrawingToolSelected) {
                        redoImageView.setOnClickListener {
                            chartViewModel.redoDrawing()
                        }
                        undoImageView.setOnClickListener {
                            chartViewModel.undoDrawing()
                        }
                        panelRecyclerView.adapter = panelAdapter
                    }

                    drawCheckBox.isChecked = isDrawingToolSelected
                    panelRecyclerView.isVisible = isDrawingToolSelected
                    redoImageView.isVisible = isDrawingToolSelected
                    undoImageView.isVisible = isDrawingToolSelected
                }
                if (drawingTool == DrawingTool.NO_TOOL) {
                    // setupInstrumentsList is triggered when the tool gets its parameters but no tool
                    // doesn't get any parameters from the library for some reason so we setup it manually here
                    panelAdapter.items = setupInstrumentsList()
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
            }
            crosshairsHUD.observe(viewLifecycleOwner) { hud ->
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
                event.getContentIfNotHandled()?.let {
                    panelAdapter.items = panelList.map { it.copy(isSelected = false) }
                }
            }
            mainViewModel.errorLiveData.observe(viewLifecycleOwner) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.warning_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
            isCrosshairsVisible.observe(viewLifecycleOwner) { value ->
                binding.crosshairLayout.root.isVisible = value
            }
            isPickerItemSelected.observe(viewLifecycleOwner) { value ->
                binding.instrumentRecyclerView.isVisible = value
            }
            navigateToDrawingToolsEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {
                    findNavController().navigate(R.id.action_mainFragment_to_drawingToolFragment)
                }
            }
            isFullscreen.observe(viewLifecycleOwner) { isFullscreen ->
                val isDrawingToolSelected = chartViewModel.drawingTool.value != DrawingTool.NONE
                if (isFullscreen) {
                    enableFullscreen(isDrawingToolSelected)
                } else {
                    disableFullscreen(isDrawingToolSelected)
                }
            }
            moveHintsAreShown.observe(viewLifecycleOwner) { areShown ->
                if (areShown) {
                    with(binding) {
                        collapseFullviewCheckBox
                            .setOnTouchListener(collapseFullviewButtonOnSwipeListener)

                        listOf(moveLeftCollapseButtonView, moveDownCollapseButtonView)
                            .forEach { view ->
                                view.isVisible = true
                                binding.root.postDelayed({
                                    view.isVisible = false
                                }, ANIMATION_MOVE_HINT_DELAY_DISAPPEAR)
                            }
                    }
                }
            }
        }
    }

    private fun onSelectItem(item: InstrumentItem) {
        val previousItem = panelList.find { it.isSelected }
        val isPickerItemSelected = chartViewModel.isPickerItemSelected.value!!
        if (item.instrument == previousItem?.instrument && isPickerItemSelected) {
            chartViewModel.disablePicker()
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
            Instrument.CLONE -> chartViewModel.cloneDrawing()
            Instrument.DELETE -> chartViewModel.deleteDrawing()
            Instrument.LAYER_MANAGEMENT -> showLayerManagementDialogue()
            Instrument.SETTINGS -> navigateToInstrumentSettings()
        }
    }

    private fun navigateToDrawingTools() {
        findNavController().navigate(R.id.action_mainFragment_to_drawingToolFragment)
    }

    private fun navigateToInstrumentSettings() {
        val direction = MainFragmentDirections
            .actionMainFragmentToDrawingToolSettingsFragment(chartViewModel.drawingTool.value!!)
        findNavController().navigate(direction)
    }

    private fun showFillColorCarousel() {
        val colors = getColors()
        // If `fillColor` color is in colors list then it should be selected
        val scrollIndex = drawingToolParameters?.fillColor?.let {
            val paramColor = convertStringColorToInt(it)
            findColorIndex(colors, paramColor)
        }
        colorsAdapter.items = colors
            .mapIndexed { index, it -> it.copy(isSelected = index == scrollIndex) }
        colorsAdapter.listener = OnSelectItemListener { colorItem ->
            chartViewModel.updateFillColor(colorItem.color)
        }
        setupInstrumentPicker(colorsAdapter, scrollIndex)
    }

    private fun showColorCarousel() {
        val colors = getColors()
        // If `color` color is in colors list then it should be selected
        val scrollIndex = drawingToolParameters?.color?.let {
            val paramColor = convertStringColorToInt(it)
            findColorIndex(colors, paramColor)
        }

        colorsAdapter.items = colors
            .mapIndexed { index, it -> it.copy(isSelected = index == scrollIndex) }
        colorsAdapter.listener = OnSelectItemListener { colorItem ->
            chartViewModel.updateColor(colorItem.color)
        }
        setupInstrumentPicker(colorsAdapter, scrollIndex)
    }

    private fun showLineTypeCarousel() {
        val lines = LineTypes.values()
            .map { LineItem(it.lineType, it.lineWidth, it.iconRes) }

        val scrollIndex = findLineIndex(
            lines,
            drawingToolParameters?.lineType,
            drawingToolParameters?.lineWidth
        )
        linesAdapter.items = lines
            .mapIndexed { index, it -> it.copy(isSelected = index == scrollIndex) }
        linesAdapter.listener = OnSelectItemListener { lineItem ->
            chartViewModel.updateLine(lineItem.lineType, lineItem.lineWidth)
        }
        setupInstrumentPicker(linesAdapter, scrollIndex)
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

    private fun toggleFullscreenViews(enterFullview: Boolean) {
        val isDrawingToolSelected = chartViewModel.drawingTool.value != DrawingTool.NONE
        with(binding) {
            toolbar.isVisible = !enterFullview
            collapseFullviewCheckBox.isVisible = enterFullview
            mainViewModel.showNavBar(!enterFullview)

            if (isDrawingToolSelected) {
                undoImageView.isVisible = !enterFullview
                redoImageView.isVisible = !enterFullview
                instrumentRecyclerView.isVisible = !enterFullview
                panelRecyclerView.isVisible = !enterFullview
            }
        }
    }

    private fun enableFullscreen(isDrawingToolSelected: Boolean) {
        with(binding) {
            toolbar.isVisible = isDrawingToolSelected
            collapseFullviewCheckBox.isVisible = !isDrawingToolSelected
            fullviewCheckBox.isVisible = true
            if (!isDrawingToolSelected) {
                mainViewModel.showNavBar(false)
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            activity?.window?.decorView?.let { decorView ->
                decorView.systemUiVisibility =
                    decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        } else {
            activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        if (!isDrawingToolSelected) {
            binding.root.postDelayed({
                chartViewModel.showMoveHints(true)
            }, ANIMATION_MOVE_HINT_DELAY_APPEAR)
        }
    }

    private fun disableFullscreen(isDrawingToolSelected: Boolean) {
        mainViewModel.showNavBar(true)
        with(binding) {
            collapseFullviewCheckBox.isVisible = false
            fullviewCheckBox.isVisible = false
            toolbar.isVisible = true

            if (isDrawingToolSelected) {
                undoImageView.isVisible = true
                redoImageView.isVisible = true
                panelRecyclerView.isVisible = true
                chartViewModel.disablePicker()
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            activity?.window?.decorView?.let { decorView ->
                decorView.systemUiVisibility =
                    decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
            }
        } else {
            activity?.window?.insetsController?.show(WindowInsets.Type.statusBars())
        }
        chartViewModel.showMoveHints(false)
    }

    private fun <VH, T : RecyclerView.Adapter<VH>> setupInstrumentPicker(
        pickerAdapter: T,
        scrollIndex: Int?
    ) {
        binding.instrumentRecyclerView.apply {
            setHasFixedSize(true)
            adapter = pickerAdapter
            post {
                scrollIndex?.let { smoothScrollToPosition(it) }
            }
        }
        chartViewModel.enablePicker()
    }

    companion object {
        private const val ANIMATION_MOVE_HINT_DELAY_APPEAR = 1000L
        private const val ANIMATION_MOVE_HINT_DELAY_DISAPPEAR = 1800L
        private const val REQUEST_CODE_MANAGE_LAYERS = 1012
    }
}
