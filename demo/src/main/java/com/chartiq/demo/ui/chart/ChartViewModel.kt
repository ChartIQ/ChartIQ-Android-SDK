package com.chartiq.demo.ui.chart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.R
import com.chartiq.demo.network.NetworkManager
import com.chartiq.demo.network.NetworkResult
import com.chartiq.demo.network.model.DrawingParameter
import com.chartiq.demo.network.model.PanelDrawingToolParameters
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.panel.model.Instrument
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem
import com.chartiq.demo.ui.chart.searchsymbol.Symbol
import com.chartiq.demo.ui.common.colorpicker.toHexStringWithHash
import com.chartiq.demo.util.Event
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.ChartLayer
import com.chartiq.sdk.model.CrosshairHUD
import com.chartiq.sdk.model.QuoteFeedParams
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.sdk.model.drawingtool.LineType
import com.chartiq.sdk.model.drawingtool.drawingmanager.DrawingManager
import com.google.gson.Gson
import kotlinx.coroutines.*

class ChartViewModel(
    private val networkManager: NetworkManager,
    private val applicationPrefs: ApplicationPrefs,
    private val chartIQHandler: ChartIQ,
    private val drawingManager: DrawingManager
) : ViewModel() {

    private val job = SupervisorJob()

    private val chartScope = CoroutineScope(job + Dispatchers.IO)

    val currentSymbol = MutableLiveData<Symbol>()

    val chartInterval = MutableLiveData<Interval>()

    val drawingTool = MutableLiveData(DrawingTool.NONE)

    @Deprecated("This logic was moved to MainViewModel class")
    val resultLiveData = MutableLiveData<ChartData>()

    @Deprecated("This logic was moved to MainViewModel class")
    val errorLiveData = MutableLiveData<Event<Unit>>()

    val resetInstrumentsLiveData = MutableLiveData<Event<Unit>>()

    val parameters = MutableLiveData<PanelDrawingToolParameters>()

    val crosshairsHUD = MutableLiveData<CrosshairHUD>()

    val isCrosshairsVisible = MutableLiveData(false)

    val isPickerItemSelected = MutableLiveData(false)

    val isFullscreen = MutableLiveData(false)

    val moveHintsAreShown = MutableLiveData(Event(false))

    val navigateToDrawingToolsEvent = MutableLiveData<Event<Unit>>()

    init {
        fetchSavedSettings()
    }

    // TODO: 19.10.20 Review
    @Deprecated("All state data that should be kept during the whole app live  and should not be attached to a concrete fragment is moved to MainViewModel")
    fun getDataFeed(params: QuoteFeedParams, callback: DataSourceCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            val applicationId = applicationPrefs.getApplicationId()
            when (val result = networkManager.fetchDataFeed(params, applicationId)) {
                is NetworkResult.Success -> resultLiveData
                    .postValue(ChartData(result.data, callback))
                is NetworkResult.Failure -> errorLiveData
                    .postValue(Event(Unit))
            }
        }
    }

    fun showMoveHints(show: Boolean) {
        if (!moveHintsAreShown.value!!.peekContent()) {
            moveHintsAreShown.value = Event(show)
        }
    }

    fun setupInstrumentsList(): List<InstrumentItem> {
        val item = DrawingTools.values().find { it.tool == drawingTool.value!! } ?: return listOf()
        val tool = item.tool

        val instrumentList = mutableListOf<InstrumentItem>()
        instrumentList.add(InstrumentItem(Instrument.DRAWING_TOOL, item.iconRes))
        with(drawingManager) {
            if (isSupportingFillColor(tool)) {
                instrumentList.add(
                    InstrumentItem(Instrument.FILL, R.drawable.ic_panel_fill)
                )
            }
            if (isSupportingLineColor(tool)) {
                instrumentList.add(
                    InstrumentItem(Instrument.COLOR, R.drawable.ic_panel_color)
                )
            }
            if (isSupportingLineType(tool)) {
                instrumentList.add(
                    InstrumentItem(Instrument.LINE_TYPE, R.drawable.ic_panel_line_type)
                )
            }
            instrumentList.add(
                InstrumentItem(Instrument.CLONE, R.drawable.ic_panel_clone)
            )
            instrumentList.add(
                InstrumentItem(Instrument.DELETE, R.drawable.ic_panel_delete)
            )
            instrumentList.add(
                InstrumentItem(Instrument.LAYER_MANAGEMENT, R.drawable.ic_panel_layers_management)
            )
            if (isSupportingSettings(tool)) {
                instrumentList.add(
                    InstrumentItem(Instrument.SETTINGS, R.drawable.ic_panel_options)
                )
            }
        }
        return instrumentList
    }

    fun toggleFullscreen() {
        isFullscreen.value = !isFullscreen.value!!
    }

    fun toggleDrawingTool() {
        if (drawingTool.value != DrawingTool.NONE) {
            drawingTool.value = DrawingTool.NONE
            chartIQHandler.disableDrawing()
            applicationPrefs.saveDrawingTool(DrawingTool.NONE)
        } else {
            navigateToDrawingToolsEvent.value = Event(Unit)
        }
    }

    fun updateFillColor(color: Int) {
        chartIQHandler.setDrawingParameter(
            DrawingParameter.FILL_COLOR.value,
            color.toHexStringWithHash()
        )
        getDrawingToolParameters()
        isPickerItemSelected.value = false
    }

    fun updateColor(color: Int) {
        chartIQHandler.setDrawingParameter(
            DrawingParameter.LINE_COLOR.value,
            color.toHexStringWithHash()
        )
        getDrawingToolParameters()
        isPickerItemSelected.value = false
    }

    fun updateLine(lineType: LineType, lineWidth: Int) {
        chartIQHandler.setDrawingParameter(DrawingParameter.LINE_TYPE.value, lineType.value)
        chartIQHandler.setDrawingParameter(DrawingParameter.LINE_WIDTH.value, lineWidth.toString())
        getDrawingToolParameters()
        isPickerItemSelected.value = false
    }

    fun cloneDrawing() {
        chartIQHandler.cloneDrawing()
        resetInstruments(REFRESH_TIME_MILLIS)
    }

    fun deleteDrawing() {
        chartIQHandler.deleteDrawing()
        resetInstruments(REFRESH_TIME_MILLIS)
    }

    fun manageLayer(layer: ChartLayer) {
        chartIQHandler.manageLayer(layer)
        resetInstruments()
    }

    fun undoDrawing() = chartIQHandler.undoDrawing {}

    fun redoDrawing() = chartIQHandler.redoDrawing {}

    fun resetInstruments(delay: Long = REFRESH_IMMEDIATE_MILLIS) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(delay)
            resetInstrumentsLiveData.postValue(Event(Unit))
        }
    }

    fun toggleCrosshairs() {
        val isVisible = isCrosshairsVisible.value!!
        isCrosshairsVisible.value = if (isVisible) {
            disableCrosshairs()
            false
        } else {
            enableCrosshairs()
            true
        }
    }

    fun onResume() {
        fetchSavedSettings()
        if (isCrosshairsVisible.value!!) {
            launchCrosshairsUpdate()
        }
    }

    fun onPause() {
        job.cancelChildren()
    }

    fun enablePicker() {
        isPickerItemSelected.value = true
    }

    fun disablePicker() {
        isPickerItemSelected.value = false
    }

    private fun getDrawingToolParameters() {
        chartIQHandler.getDrawingParameters(drawingTool.value!!) { parameters ->
            val gson = Gson()
            val jsonElement = gson.toJsonTree(parameters)
            this.parameters.value =
                gson.fromJson(jsonElement, PanelDrawingToolParameters::class.java)
        }
    }

    private fun fetchSavedSettings() {
        val symbol = applicationPrefs.getChartSymbol()
        if (currentSymbol.value != symbol) {
            currentSymbol.value = symbol
            chartIQHandler.setSymbol(symbol.value)
        }
        val interval = applicationPrefs.getChartInterval()
        if (chartInterval.value != interval) {
            chartInterval.value = interval
            chartIQHandler.setPeriodicity(
                interval.getPeriod(),
                interval.getInterval(),
                interval.getTimeUnit()
            )
        }
        val tool = applicationPrefs.getDrawingTool()
        if(drawingTool.value != tool) {
            drawingTool.value = tool
            if (drawingTool.value != DrawingTool.NONE) {
                chartIQHandler.enableDrawing(drawingTool.value!!)
                getDrawingToolParameters()
            }
        }
    }

    private fun enableCrosshairs() {
        launchCrosshairsUpdate()
        chartIQHandler.enableCrosshairs()
    }

    private fun disableCrosshairs() {
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

    class ChartViewModelFactory(
        private val argNetworkManager: NetworkManager,
        private val argApplicationPrefs: ApplicationPrefs,
        private val argChartIQHandler: ChartIQ,
        private val argDrawingManager: DrawingManager
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(
                    NetworkManager::class.java,
                    ApplicationPrefs::class.java,
                    ChartIQ::class.java,
                    DrawingManager::class.java
                )
                .newInstance(
                    argNetworkManager,
                    argApplicationPrefs,
                    argChartIQHandler,
                    argDrawingManager
                )
        }
    }

    companion object {
        private const val CROSSHAIRS_UPDATE_PERIOD = 300L

        private const val REFRESH_TIME_MILLIS = 400L
        private const val REFRESH_IMMEDIATE_MILLIS = 0L
    }
}
