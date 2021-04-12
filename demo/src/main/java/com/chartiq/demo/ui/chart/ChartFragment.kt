package com.chartiq.demo.ui.chart

import android.animation.LayoutTransition
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.ServiceLocator
import com.chartiq.demo.databinding.FragmentChartBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.network.model.PanelDrawingToolParameters
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
import com.chartiq.demo.ui.common.colorpicker.ColorItem
import com.chartiq.demo.ui.common.colorpicker.ColorsAdapter
import com.chartiq.demo.ui.common.colorpicker.convertStringColorToInt
import com.chartiq.demo.ui.common.colorpicker.findColorIndex
import com.chartiq.demo.ui.common.linepicker.LineAdapter
import com.chartiq.demo.ui.common.linepicker.LineItem
import com.chartiq.demo.ui.common.linepicker.findLineIndex
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.TimeUnit
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.sdk.model.drawingtool.drawingmanager.ChartIQDrawingManager
import java.util.*

class ChartFragment : Fragment(),
        ChooseSymbolFragment.DialogFragmentListener, ChooseIntervalFragment.DialogFragmentListener {

    private val chartIQ: ChartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val localizationManager by lazy {
        (requireActivity().application as ChartIQApplication).localizationManager
    }
    private lateinit var binding: FragmentChartBinding
    private lateinit var panelList: List<InstrumentItem>
    private var drawingToolParameters: PanelDrawingToolParameters? = null
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
        chartViewModel.onResume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (chartViewModel.moveHintsAreShown.value?.peekContent() == true) {
            hideCollapseButtonArrowsWithoutAnimation()
        }
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
                chartViewModel.toggleFullscreenViews(true)
            }
            collapseFullviewCheckBox.apply {
                setOnClickListener {
                    chartViewModel.toggleFullscreenViews(false)
                    hideCollapseButtonArrowsWithoutAnimation()
                }
                val touchListener = CollapseButtonOnSwipeTouchListener(root, requireContext()) {
                    listOf(moveLeftCollapseButtonView, moveDownCollapseButtonView).forEach {
                        it.isVisible = false
                    }
                }
                setOnTouchListener(touchListener)
            }
        }
        setupCrosshairsLayout()

        with(mainViewModel) {
            symbol.observe(viewLifecycleOwner) { symbol ->
                binding.symbolButton.text = symbol.value
            }
            interval.observe(viewLifecycleOwner) { chartInterval ->
                chartInterval.apply {
                    var fullPeriodicity = period * interval
                    if(timeUnit == TimeUnit.HOUR) {
                        fullPeriodicity /= 60
                    }
                    val shortTimeUnitName = localizationManager.getTranslationFromValue(
                            timeUnit.toString().first().toString(),
                            requireContext()
                    )
                    binding.intervalButton.text = when (timeUnit) {
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
            isFullscreen.observe(viewLifecycleOwner) { isFullscreen ->
                val isDrawingToolSelected = chartViewModel.drawingTool.value != DrawingTool.NONE
                if (isFullscreen) {
                    enableFullscreen(isDrawingToolSelected)
                } else {
                    disableFullscreen(isDrawingToolSelected)
                }
            }
        }
        with(chartViewModel) {
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
                if (!isDrawingToolSelected) {
                    binding.instrumentRecyclerView.isVisible = false
                    panelAdapter.items = listOf()
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
            isCrosshairsVisible.observe(viewLifecycleOwner) { value ->
                binding.crosshairCheckBox.apply {
                    if (isChecked != value) {
                        isChecked = value
                    }
                }
                binding.crosshairLayout.root.isVisible = value
                //update the translations
                setupCrosshairsLayout()
            }
            isPickerItemSelected.observe(viewLifecycleOwner) { value ->
                binding.instrumentRecyclerView.isVisible = value
            }
            navigateToDrawingToolsEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {
                    findNavController().navigate(R.id.action_mainFragment_to_drawingToolFragment)
                }
            }
            moveHintsAreShown.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { areShown ->
                    if (areShown) {
                        with(binding) {
                            listOf(moveLeftCollapseButtonView, moveDownCollapseButtonView)
                                    .forEach { view ->
                                        view.isVisible = true
                                        root.postDelayed({
                                            view.isVisible = false
                                        }, ANIMATION_MOVE_HINT_DELAY_DISAPPEAR)
                                    }
                        }
                    }
                }
            }
            measureToolInfo.observe(viewLifecycleOwner) { measure ->
                binding.measureToolInfoTextView.apply {
                    with (measure) {
                        text = when (oldValue) {
                            null -> ""
                            else -> if(newValue.isEmpty()) {
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
            isCollapsed.observe(viewLifecycleOwner) { isFullview ->
                with(binding) {
                    forceCloseCrosshairs()
                    toolbar.isVisible = !isFullview
                    collapseFullviewCheckBox.isVisible = isFullview
                    mainViewModel.updateFullView(!isFullview)
                    if(isFullview) {
                        hideStatusBar()
                    } else {
                        showStatusBar()
                    }

                    val isDrawingToolSelected = chartViewModel.drawingTool.value != DrawingTool.NONE
                    if (isDrawingToolSelected) {
                        undoImageView.isVisible = !isFullview
                        redoImageView.isVisible = !isFullview
                        instrumentRecyclerView.isVisible = !isFullview && instrumentRecyclerView.adapter?.itemCount != null
                        panelRecyclerView.isVisible = !isFullview
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

    private fun getColors(): List<ColorItem> {
        val colors = resources.obtainTypedArray(R.array.colors)
        val colorsList = mutableListOf<ColorItem>()
        for (index in 0 until colors.length()) {
            colorsList.add(ColorItem(colors.getColor(index, Color.WHITE)))
        }
        colors.recycle()
        return colorsList
    }

    private fun enableFullscreen(isDrawingToolSelected: Boolean) {
        with(binding) {
            forceCloseCrosshairs()
            toolbar.isVisible = isDrawingToolSelected
            collapseFullviewCheckBox.isVisible = !isDrawingToolSelected
            fullviewCheckBox.isVisible = true
            if (!isDrawingToolSelected) {
                mainViewModel.updateFullView(false)
            }
        }
        hideStatusBar()
        if (!isDrawingToolSelected) {
            binding.root.postDelayed({
                chartViewModel.showMoveHints(true)
            }, ANIMATION_MOVE_HINT_DELAY_APPEAR)
        }
    }

    private fun disableFullscreen(isDrawingToolSelected: Boolean) {
        mainViewModel.updateFullView(true)
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
        showStatusBar()
        chartViewModel.showMoveHints(false)
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

    private fun forceCloseCrosshairs() {
        if (chartViewModel.isCrosshairsVisible.value == true) {
            binding.crosshairCheckBox.isChecked = false
            chartViewModel.toggleCrosshairs()
        }
    }

    private fun hideCollapseButtonArrowsWithoutAnimation() {
        val layoutTransition = binding.parentLayout.layoutTransition
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING)
        binding.moveLeftCollapseButtonView.isVisible = false
        binding.moveDownCollapseButtonView.isVisible = false
        layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING)
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
        private const val REQUEST_CODE_SEARCH_SYMBOL = 1013
        private const val REQUEST_CODE_CHOOSE_INTERVAL = 1014
    }
}
