package com.chartiq.demo.ui.chart

import android.animation.LayoutTransition
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.ServiceLocator
import com.chartiq.demo.databinding.FragmentChartBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.network.model.PanelDrawingToolParameter
import com.chartiq.demo.ui.MainFragmentDirections
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.chart.fullview.CollapseButtonOnSwipeTouchListener
import com.chartiq.demo.ui.chart.interval.ChooseIntervalFragment
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.panel.InstrumentPanelAdapter
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.chart.panel.model.Instrument
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem
import com.chartiq.demo.ui.chart.searchsymbol.ChooseSymbolFragment
import com.chartiq.demo.ui.chart.searchsymbol.Symbol
import com.chartiq.demo.ui.common.colorpicker.*
import com.chartiq.demo.ui.common.linepicker.LineAdapter
import com.chartiq.demo.ui.common.linepicker.LineItem
import com.chartiq.demo.ui.common.linepicker.findLineIndex
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.TimeUnit
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.sdk.model.drawingtool.drawingmanager.ChartIQDrawingManager
import java.util.*

class ChartFragment : Fragment(),
    ChooseSymbolFragment.DialogFragmentListener,
    ChooseIntervalFragment.DialogFragmentListener {

    private val chartIQ: ChartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val localizationManager by lazy {
        (requireActivity().application as ChartIQApplication).localizationManager
    }
    private lateinit var binding: FragmentChartBinding

    private val colorsAdapter by lazy { ColorsAdapter() }

    private val linesAdapter by lazy { LineAdapter() }

    private val panelAdapter: InstrumentPanelAdapter by lazy { InstrumentPanelAdapter() }

    private val mainViewModel: MainViewModel by activityViewModels(factoryProducer = {
        MainViewModel.ViewModelFactory(
            ChartIQNetworkManager(),
            (requireActivity().application as ServiceLocator).applicationPreferences,
            chartIQ,
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        )
    })

    private val chartViewModel: ChartViewModel by viewModels(factoryProducer = {
        ChartViewModel.ChartViewModelFactory(
            (requireActivity().application as ServiceLocator).applicationPreferences,
            chartIQ,
            ChartIQDrawingManager()
        )
    })
    private val pickerColors: List<ColorItem> by lazy {
        resources.getPickerColors()
    }
    private var collapseButtonWasMoved: Boolean = false


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

    private fun setChartIQView() {
        chartIQ.chartView.apply {
            (parent as? FrameLayout)?.removeAllViews()
            binding.chartIqView.addView(this)
        }
    }

    override fun onResume() {
        super.onResume()
        chartViewModel.onResume(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }

    override fun onPause() {
        super.onPause()
        chartViewModel.onPause()
        showStatusBar()
    }

    override fun onChooseSymbol(symbol: Symbol) =
        mainViewModel.updateSymbol(symbol)

    override fun onChooseInterval(interval: Interval) =
        mainViewModel.updateInterval(interval)

    private fun setupViews() {
        with(binding) {
            root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

            setupCrosshairsLayout()

            symbolButton.setOnClickListener {
                navigateToSearchSymbol()
            }
            intervalButton.setOnClickListener {
                navigateToChooseInterval()
            }
            compareCheckBox.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_compareFragment)
            }
            drawCheckBox.setOnClickListener {
                chartViewModel.toggleDrawingTool()
            }
            crosshairCheckBox.setOnClickListener {
                chartViewModel.toggleCrosshairs()
            }
            fullviewCheckBox.setOnClickListener {
                chartViewModel.onFullViewClicked()
            }
            collapseFullviewCheckBox.apply {
                setOnClickListener {
                    chartViewModel.onCollapseFullViewClicked()
                }

                val touchListener = CollapseButtonOnSwipeTouchListener(root, requireContext()) {
                    listOf(moveLeftCollapseButtonView, moveDownCollapseButtonView).forEach {
                        it.isVisible = false
                    }
                }
                setOnTouchListener(touchListener)
            }
            redoImageView.setOnClickListener {
                chartViewModel.redoDrawing()
            }
            undoImageView.setOnClickListener {
                chartViewModel.undoDrawing()
            }

            with(mainViewModel) {
                symbol.observe(viewLifecycleOwner) { symbol ->
                    symbolButton.text = symbol.value
                }
                interval.observe(viewLifecycleOwner) { chartInterval ->
                    chartInterval.apply {
                        var fullPeriodicity = period * interval
                        if (timeUnit == TimeUnit.HOUR) {
                            fullPeriodicity /= 60
                        }
                        val shortTimeUnitName = localizationManager.getTranslationFromValue(
                            timeUnit.toString().first().toString(),
                            requireContext()
                        )
                        intervalButton.text = when (timeUnit) {
                            TimeUnit.SECOND,
                            TimeUnit.MINUTE -> {
                                "$fullPeriodicity${shortTimeUnitName.toLowerCase(Locale.getDefault())}"
                            }
                            else -> "$fullPeriodicity${shortTimeUnitName}"
                        }
                    }
                }
                errorLiveData.observe(viewLifecycleOwner) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.general_warning_something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            with(chartViewModel) {
                chartViewState.observe(viewLifecycleOwner) { state ->
                    when (state) {
                        is ChartViewState.FullView -> {
                            toolbar.isVisible = false
                            collapseFullviewCheckBox.isVisible = true
                            fullviewCheckBox.isVisible = false
                            animateArrows()
                            hideStatusBar()
                        }
                        is ChartViewState.Simple -> {
                            toolbar.isVisible = true
                            collapseFullviewCheckBox.isVisible = false
                            fullviewCheckBox.isVisible = state is ChartViewState.Simple.Landscape
                            showStatusBar()
                            hideCollapseButtonArrowsWithoutAnimation()
                        }
                    }
                    mainViewModel.updateChartState(state)
                }
                crosshairsEnabled.observe(viewLifecycleOwner) { enabled ->
                    crosshairLayout.root.isVisible = enabled
                    crosshairCheckBox.isChecked = enabled

                }
                panelToolParameters.observe(viewLifecycleOwner) { panelTools ->
                    updateInstrumentPanel(panelTools)
                    toggleDrawingToolViews(panelTools.drawingTool != DrawingTool.NONE)
                }

                crosshairsHUD.observe(viewLifecycleOwner) { hud ->
                    with(crosshairLayout) {
                        priceValueTextView.text = hud.price
                        volValueTextView.text = hud.volume
                        openValueTextView.text = hud.open
                        highValueTextView.text = hud.high
                        closeValueTextView.text = hud.close
                        lowValueTextView.text = hud.low
                    }
                }
                navigateToDrawingToolsEvent.observe(viewLifecycleOwner) { event ->
                    event.getContentIfNotHandled()?.let {
                        findNavController().navigate(R.id.action_mainFragment_to_drawingToolFragment)
                    }
                }

                measureToolInfo.observe(viewLifecycleOwner) { measure ->
                    binding.measureToolInfoTextView.apply {
                        with(measure) {
                            text = when (oldValue) {
                                null -> ""
                                else -> if (newValue.isEmpty()) {
                                    if (oldValue.isEmpty()) {
                                        ""
                                    } else {
                                        oldValue
                                    }
                                } else {
                                    newValue
                                }
                            }
                            isVisible = text.isNotEmpty()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToSearchSymbol() {
        val dialog = ChooseSymbolFragment.getInstance()
        dialog.setTargetFragment(this, REQUEST_CODE_SEARCH_SYMBOL)
        dialog.show(parentFragmentManager, ChooseSymbolFragment.DIALOG_TAG)
    }

    private fun navigateToChooseInterval() {
        mainViewModel.interval.value?.let { interval ->
            val dialog = ChooseIntervalFragment.getInstance(interval)
            dialog.setTargetFragment(this, REQUEST_CODE_CHOOSE_INTERVAL)
            dialog.show(parentFragmentManager, null)
        }
    }

    private fun setupCrosshairsLayout() {
        with(binding.crosshairLayout) {
            priceLabelTextView.text =
                String.format(
                    getString(R.string.crosshair_full_label),
                    getString(R.string.crosshair_label_price)
                )
            volLabelTextView.text =
                String.format(
                    getString(R.string.crosshair_full_label),
                    getString(R.string.crosshair_label_vol)
                )
            openLabelTextView.text =
                String.format(
                    getString(R.string.crosshair_full_label),
                    getString(R.string.crosshair_label_open)
                )
            highLabelTextView.text =
                String.format(
                    getString(R.string.crosshair_full_label),
                    getString(R.string.crosshair_label_high)
                )
            closeLabelTextView.text =
                String.format(
                    getString(R.string.crosshair_full_label),
                    getString(R.string.crosshair_label_close)
                )
            lowLabelTextView.text =
                String.format(
                    getString(R.string.crosshair_full_label),
                    getString(R.string.crosshair_label_low)
                )
            openLabelTextView.text =
                String.format(
                    getString(R.string.crosshair_full_label),
                    getString(R.string.crosshair_label_open)
                )
        }
    }

    private fun onPanelToolSelected(
        clickedItem: InstrumentItem,
        panelSettings: DrawingToolPanelSettings,
        drawingTool: DrawingTool?
    ) {
        binding.instrumentRecyclerView.isVisible = !clickedItem.isSelected
        if (clickedItem.isSelected) {
            chartViewModel.onPanelToolSelected(null)
            return
        }

        chartViewModel.onPanelToolSelected(clickedItem)

        processInstrumentSelectedEvent(clickedItem, panelSettings, drawingTool)
    }

    private fun processInstrumentSelectedEvent(
        clickedItem: InstrumentItem,
        panelSettings: DrawingToolPanelSettings,
        drawingTool: DrawingTool?
    ) {
        when (clickedItem.instrument) {
            Instrument.DRAWING_TOOL -> chartViewModel.onDrawingToolOptionClick()
            Instrument.FILL -> showFillColorCarousel(panelSettings.parameter)
            Instrument.COLOR -> showColorCarousel(panelSettings.parameter)
            Instrument.LINE_TYPE -> showLineTypeCarousel(panelSettings.parameter)
            Instrument.SETTINGS -> navigateToInstrumentSettings(drawingTool!!)

        }
    }

    private fun navigateToInstrumentSettings(drawingTool: DrawingTool) {
        val direction = MainFragmentDirections
            .actionMainFragmentToDrawingToolSettingsFragment(drawingTool)
        findNavController().navigate(direction)
    }

    private fun showFillColorCarousel(drawingToolParameter: PanelDrawingToolParameter?) {
        // If `fillColor` color is in colors list then it should be selected
        val scrollIndex = drawingToolParameter?.fillColor?.let {
            val paramColor = convertStringColorToInt(it, resources)
            findColorIndex(pickerColors, paramColor)
        }
        colorsAdapter.items = pickerColors
            .mapIndexed { index, it -> it.copy(isSelected = index == scrollIndex) }

        colorsAdapter.listener = OnSelectItemListener { colorItem ->
            binding.instrumentRecyclerView.isVisible = false
            chartViewModel.updateFillColor(colorItem.color)
        }
        setupInstrumentPicker(colorsAdapter, scrollIndex)
    }

    private fun showColorCarousel(drawingToolParameter: PanelDrawingToolParameter?) {
        // If `color` color is in colors list then it should be selected
        val scrollIndex = drawingToolParameter?.color?.let {
            val paramColor = convertStringColorToInt(it, resources)
            findColorIndex(pickerColors, paramColor)
        }

        colorsAdapter.items = pickerColors
            .mapIndexed { index, it -> it.copy(isSelected = index == scrollIndex) }
        colorsAdapter.listener = OnSelectItemListener { colorItem ->
            binding.instrumentRecyclerView.isVisible = false
            chartViewModel.updateColor(colorItem.color)
        }
        setupInstrumentPicker(colorsAdapter, scrollIndex)
    }

    private fun showLineTypeCarousel(drawingToolParameter: PanelDrawingToolParameter?) {
        val lines = LineTypes.values()
            .map { LineItem(it.lineType, it.lineWidth, it.iconRes) }

        val scrollIndex = findLineIndex(
            lines,
            drawingToolParameter?.lineType,
            drawingToolParameter?.lineWidth
        )
        linesAdapter.items = lines
            .mapIndexed { index, it -> it.copy(isSelected = index == scrollIndex) }
        linesAdapter.listener = OnSelectItemListener { lineItem ->
            binding.instrumentRecyclerView.isVisible = false
            chartViewModel.updateLine(lineItem.lineType, lineItem.lineWidth)
        }
        setupInstrumentPicker(linesAdapter, scrollIndex)
    }


    private fun showStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            activity?.window?.decorView?.let { decorView ->
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                decorView.systemUiVisibility =
                    decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
            }
        } else {
            activity?.window?.insetsController?.show(WindowInsets.Type.statusBars())
        }
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            activity?.window?.decorView?.let { decorView ->
                activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                decorView.systemUiVisibility =
                    decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        } else {
            activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
        }
    }

    private fun animateArrows() {
        if (!collapseButtonWasMoved) {
            listOf(binding.moveLeftCollapseButtonView, binding.moveDownCollapseButtonView)
                .forEach { view ->
                    view.isVisible = true
                    binding.root.postDelayed({
                        view.isVisible = false
                    }, ANIMATION_MOVE_HINT_DELAY_DISAPPEAR)
                }
        }
    }

    private fun hideCollapseButtonArrowsWithoutAnimation() {
        val layoutTransition = binding.parentLayout.layoutTransition
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING)
        binding.moveLeftCollapseButtonView.isVisible = false
        binding.moveDownCollapseButtonView.isVisible = false
        layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING)
    }

    private fun updateInstrumentPanel(toolPanelSettings: DrawingToolPanelSettings) {
        if (toolPanelSettings.parameter == null || toolPanelSettings.drawingTool == DrawingTool.NONE) {
            binding.instrumentRecyclerView.isVisible = false
        } else {
            toolPanelSettings.let { panelSettings ->
                val panelList = panelSettings.instrumentItems
                panelAdapter.parameter = panelSettings.parameter
                panelAdapter.items = panelList
                panelAdapter.listener = OnSelectItemListener { instrumentItem ->
                    onPanelToolSelected(
                        instrumentItem,
                        panelSettings,
                        toolPanelSettings.drawingTool
                    )
                }
                binding.panelRecyclerView.adapter = panelAdapter
                if (panelSettings.instrumentItems.any { it.isSelected && it.instrument != Instrument.SETTINGS && it.instrument != Instrument.DRAWING_TOOL }) {
                    binding.instrumentRecyclerView.isVisible = true
                    processInstrumentSelectedEvent(
                        panelSettings.instrumentItems.first { it.isSelected },
                        panelSettings,
                        toolPanelSettings.drawingTool
                    )
                }
            }
        }
    }

    private fun toggleDrawingToolViews(value: Boolean) = with(binding) {
        drawCheckBox.isChecked = value
        panelRecyclerView.isVisible = value
        redoImageView.isVisible = value
        undoImageView.isVisible = value
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
    }

    companion object {
        private const val ANIMATION_MOVE_HINT_DELAY_DISAPPEAR = 1800L
        private const val REQUEST_CODE_SEARCH_SYMBOL = 1013
        private const val REQUEST_CODE_CHOOSE_INTERVAL = 1014
    }
}
