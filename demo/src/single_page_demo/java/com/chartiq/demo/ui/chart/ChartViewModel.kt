package com.chartiq.demo.ui.chart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.R
import com.chartiq.demo.network.model.PanelDrawingToolParameter
import com.chartiq.demo.ui.chart.panel.model.Instrument
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem
import com.chartiq.demo.ui.common.colorpicker.toHexStringWithHash
import com.chartiq.demo.util.Event
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.CrosshairHUD
import com.chartiq.sdk.model.drawingtool.DrawingParameterType
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.sdk.model.drawingtool.LineType
import com.chartiq.sdk.model.drawingtool.drawingmanager.DrawingManager
import com.google.gson.Gson
import kotlinx.coroutines.*

class ChartViewModel(
    private val applicationPrefs: ApplicationPrefs,
    private val chartIQHandler: ChartIQ,
    private val drawingManager: DrawingManager,
) : ViewModel() {

    private val job = Job()

    private val chartScope = CoroutineScope(job + Dispatchers.IO)

    val chartViewState = MutableLiveData<ChartViewState?>()

    val crosshairsHUD = MutableLiveData<CrosshairHUD>()

    val navigateToDrawingToolsEvent = MutableLiveData<Event<Unit>>()

    val measureToolInfo = MutableLiveData(MeasureItem(null, ""))

    val menuExpanded = Transformations.map(chartViewState) {
        when (it) {
            is ChartViewState.FullView -> false
            ChartViewState.Simple.Landscape -> false
            is ChartViewState.Simple.Portrait -> it.menuExpanded
            null -> false
        }
    }

    val crosshairsEnabled = MutableLiveData(false)

    val panelToolParameters = MutableLiveData(
        DrawingToolPanelSettings(
            drawingTool = DrawingTool.NONE,
            parameter = null,
            instrumentItems = emptyList()
        )
    )

    init {
        chartIQHandler.addMeasureListener { measureValue ->
            val drawingTool = panelToolParameters.value!!.drawingTool
            if (drawingTool != DrawingTool.NONE) {
                with(measureToolInfo) {
                    val currentValue = value?.newValue
                    /*val newValue = if (measureValue.isEmpty() && !value?.oldValue.isNullOrEmpty()) {
                        value?.oldValue!!
                    } else {
                        measureValue
                    }*/
                    val newValue = measureValue
                    postValue(MeasureItem(currentValue, newValue))
                }
            }
        }
        fetchCrosshairsState()
    }

    private fun fetchCrosshairsState() {
        chartIQHandler.isCrosshairsEnabled { enabled ->
            when (chartViewState.value) {
                is ChartViewState.Simple -> {
                    crosshairsEnabled.value = enabled
                }
                else -> {
                    // do nothing
                }
            }
            if (enabled) {
                launchCrosshairsUpdate()
            }
        }
    }

    private fun initViewState(isLandscape: Boolean) {
        val drawingTool = applicationPrefs.getDrawingTool()
        val savedChartViewState = applicationPrefs.getChartViewState()

        if (isLandscape) {
            if (drawingTool != DrawingTool.NONE || savedChartViewState is ChartViewState.Simple.Landscape) {
                chartViewState.value = ChartViewState.Simple.Landscape
            } else {
                chartViewState.value = ChartViewState.FullView(true)
                disableCrosshairs()
            }
        } else {
            chartViewState.value = ChartViewState.Simple.Portrait(
                (savedChartViewState as? ChartViewState.Simple.Portrait)?.menuExpanded ?: false
            )
        }
        applicationPrefs.setChartViewState(chartViewState.value!!)
    }

    fun toggleCrosshairs() {
        when (chartViewState.value) {
            is ChartViewState.Simple -> {
                val oldCrosshairs = crosshairsEnabled.value ?: false
                crosshairsEnabled.value = !oldCrosshairs
                updateCrosshairsHandling(!oldCrosshairs)
            }
            else -> {
                // do nothing, illegal state
            }
        }
    }

    private fun updateCrosshairsHandling(shouldMonitor: Boolean) {
        when (shouldMonitor) {
            true -> enableCrosshairs()
            false -> disableCrosshairs()
        }
    }

    private fun setupInstrumentsList(
        selectedTool: DrawingTool,
        selectedInstrument: InstrumentItem?
    ): List<InstrumentItem> {
        val item = DrawingTools.values().find { it.tool == selectedTool } ?: return listOf()
        val tool = item.tool

        val instrumentList = mutableListOf<InstrumentItem>()
        instrumentList.add(
            InstrumentItem(
                Instrument.DRAWING_TOOL,
                item.iconRes,
                selectedInstrument?.instrument == Instrument.DRAWING_TOOL
            )
        )
        with(drawingManager) {
            if (isSupportingFillColor(tool)) {
                instrumentList.add(
                    InstrumentItem(
                        Instrument.FILL, R.drawable.ic_panel_fill,
                        selectedInstrument?.instrument == Instrument.FILL
                    )
                )
            }
            if (isSupportingLineColor(tool)) {
                instrumentList.add(
                    InstrumentItem(
                        Instrument.COLOR, R.drawable.ic_panel_color,
                        selectedInstrument?.instrument == Instrument.COLOR
                    )
                )
            }
            if (isSupportingLineType(tool)) {
                instrumentList.add(
                    InstrumentItem(
                        Instrument.LINE_TYPE,
                        R.drawable.ic_panel_line_type,
                        selectedInstrument?.instrument == Instrument.LINE_TYPE
                    )
                )
            }
            if (isSupportingSettings(tool)) {
                instrumentList.add(
                    InstrumentItem(
                        Instrument.SETTINGS,
                        R.drawable.ic_panel_options,
                        selectedInstrument?.instrument == Instrument.SETTINGS
                    )
                )
            }
        }
        return instrumentList
    }

    fun toggleDrawingTool() {
        when (chartViewState.value) {
            is ChartViewState.Simple -> {
                val drawingTool = panelToolParameters.value!!.drawingTool
                if (drawingTool != DrawingTool.NONE) {
                    panelToolParameters.value = DrawingToolPanelSettings(
                        drawingTool = DrawingTool.NONE,
                        parameter = null,
                        instrumentItems = emptyList()
                    )
                    chartIQHandler.disableDrawing()
                    applicationPrefs.saveDrawingTool(DrawingTool.NONE)
                    measureToolInfo.value = MeasureItem(null, "")
                } else {
                    navigateToDrawingToolsEvent.value = Event(Unit)
                }
            }
            else -> {
                // do nothing, illegal state
            }
        }
    }

    fun updateFillColor(color: Int) {
        chartIQHandler.setDrawingParameter(
            DrawingParameterType.FILL_COLOR.value,
            color.toHexStringWithHash()
        )
        updateTool()
    }

    fun updateColor(color: Int) {
        chartIQHandler.setDrawingParameter(
            DrawingParameterType.LINE_COLOR.value,
            color.toHexStringWithHash()
        )
        updateTool()
    }

    fun updateLine(lineType: LineType, lineWidth: Int) {
        chartIQHandler.setDrawingParameter(DrawingParameterType.LINE_TYPE.value, lineType.value)
        chartIQHandler.setDrawingParameter(
            DrawingParameterType.LINE_WIDTH.value,
            lineWidth.toString()
        )
        updateTool()
    }

    fun undoDrawing() = chartIQHandler.undoDrawing {}

    fun redoDrawing() = chartIQHandler.redoDrawing {}

    fun onResume(isLandscape: Boolean) {
        fetchDrawingTool()
        fetchCrosshairsState()
        initViewState(isLandscape)
    }

    fun onPause() {
        job.cancelChildren()
        applicationPrefs.setChartViewState(chartViewState.value!!)
    }

    fun toggleExtraMenu() {
        val menuExpandedOld = menuExpanded.value ?: false
        when (val chartState = chartViewState.value) {
            is ChartViewState.Simple.Portrait -> {
                chartViewState.value = chartState.copy(menuExpanded = !menuExpandedOld)
            }
            else -> {
                // do nothing, illegal state
            }
        }
    }

    private fun updateTool() {
        updatePanelDrawingToolParameters(
            panelToolParameters.value!!.drawingTool
        ) { panelDrawingToolParameter: PanelDrawingToolParameter ->
            when (chartViewState.value) {
                is ChartViewState.Simple -> {
                    panelToolParameters.value = panelToolParameters.value?.copy(
                        parameter = panelDrawingToolParameter,
                        instrumentItems = panelToolParameters.value?.instrumentItems?.map {
                            it.copy(
                                isSelected = false
                            )
                        }
                            ?: emptyList()
                    )
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun updatePanelDrawingToolParameters(
        tool: DrawingTool,
        block: (PanelDrawingToolParameter) -> Unit
    ) {
        chartIQHandler.getDrawingParameters(tool) { parameters ->
            val gson = Gson()
            val jsonElement = gson.toJsonTree(parameters)
            block(gson.fromJson(jsonElement, PanelDrawingToolParameter::class.java))
        }
    }

    private fun fetchDrawingTool() {
        val tool = applicationPrefs.getDrawingTool()
        if (tool != DrawingTool.NONE) {
            chartIQHandler.enableDrawing(tool)
            updatePanelDrawingToolParameters(tool) { panelDrawingToolParameters ->
                when (chartViewState.value) {
                    is ChartViewState.Simple -> {
                        val settings = DrawingToolPanelSettings(
                            parameter = panelDrawingToolParameters,
                            instrumentItems = setupInstrumentsList(
                                tool,
                                panelToolParameters.value?.instrumentItems?.firstOrNull { it.isSelected }),
                            drawingTool = tool
                        )
                        panelToolParameters.value = settings
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
        } else {
            when (chartViewState.value) {
                is ChartViewState.Simple -> {
                    panelToolParameters.value = DrawingToolPanelSettings(
                        parameter = null,
                        instrumentItems = setupInstrumentsList(
                            tool,
                            panelToolParameters.value?.instrumentItems?.firstOrNull { it.isSelected }),
                        drawingTool = tool
                    )
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun enableCrosshairs() {
        crosshairsEnabled.value = true
        launchCrosshairsUpdate()
        chartIQHandler.enableCrosshairs()
    }

    private fun disableCrosshairs() {
        crosshairsEnabled.value = false
        job.cancelChildren()
        chartIQHandler.disableCrosshairs()
    }

    private fun getHUDDetails() {
        chartIQHandler.getHUDDetails { hud ->
            crosshairsHUD.value = hud
        }
    }

    private fun launchCrosshairsUpdate() {
        chartScope.launch {
            while (true) {
                delay(CROSSHAIRS_UPDATE_PERIOD)
                withContext(Dispatchers.Main) {
                    getHUDDetails()
                }
            }
        }
    }

    fun onFullViewClicked() {
        chartViewState.value = ChartViewState.FullView(false)
        disableCrosshairs()
        disableDrawingTools()
    }

    private fun disableDrawingTools() {
        panelToolParameters.value = DrawingToolPanelSettings(
            drawingTool = DrawingTool.NONE,
            parameter = null,
            instrumentItems = emptyList()
        )
        chartIQHandler.disableDrawing()
        applicationPrefs.saveDrawingTool(DrawingTool.NONE)
        measureToolInfo.value = MeasureItem(null, "")
    }

    fun onCollapseFullViewClicked() {
        chartViewState.value = ChartViewState.Simple.Landscape
        disableDrawingTools()
        disableCrosshairs()
    }

    fun onPanelToolSelected(item: InstrumentItem?) {
        when (chartViewState.value) {
            is ChartViewState.Simple -> {

                val instrumentItems: List<InstrumentItem> = panelToolParameters.value?.instrumentItems ?: emptyList()
                val newToolParameters = panelToolParameters.value?.copy(
                    instrumentItems = instrumentItems.map {
                        it.copy(
                            isSelected = (it == item
                                    && it.instrument != Instrument.SETTINGS
                                    && it.instrument != Instrument.DRAWING_TOOL)
                        )
                    }
                )
                panelToolParameters.value = newToolParameters
            }
            else -> {
                // do nothing, illegal state
            }
        }
    }

    fun onDrawingToolOptionClick() {
        navigateToDrawingToolsEvent.value = Event(Unit)
    }

    class ChartViewModelFactory(
        private val argApplicationPrefs: ApplicationPrefs,
        private val argChartIQHandler: ChartIQ,
        private val argDrawingManager: DrawingManager,
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(
                    ApplicationPrefs::class.java,
                    ChartIQ::class.java,
                    DrawingManager::class.java,
                )
                .newInstance(
                    argApplicationPrefs,
                    argChartIQHandler,
                    argDrawingManager,
                )
        }
    }

    companion object {
        private const val CROSSHAIRS_UPDATE_PERIOD = 300L
    }
}

