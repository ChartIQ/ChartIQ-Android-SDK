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
import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolItem
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.panel.model.Instrument
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem
import com.chartiq.demo.ui.chart.searchsymbol.Symbol
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.model.ChartLayer
import com.chartiq.sdk.model.CrosshairHUD
import com.chartiq.sdk.model.DataMethod
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.sdk.model.QuoteFeedParams
import com.chartiq.demo.network.model.PanelDrawingToolParameters
import com.chartiq.demo.ui.common.colorpicker.toHexStringWithHash
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

    val currentSymbol = MutableLiveData<Symbol>()

    val chartInterval = MutableLiveData<Interval>()

    val drawingTool = MutableLiveData<DrawingTool>()

    val resultLiveData = MutableLiveData<ChartData>()

    val errorLiveData = MutableLiveData<Unit>()

    val resetInstrumentsLiveData = MutableLiveData<Unit>()

    val parameters = MutableLiveData<PanelDrawingToolParameters>()

    val crosshairHUD = MutableLiveData<CrosshairHUD>()

    fun getDataFeed(params: QuoteFeedParams, callback: DataSourceCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            val applicationId = applicationPrefs.getApplicationId()
            when (val result = networkManager.fetchDataFeed(params, applicationId)) {
                is NetworkResult.Success -> resultLiveData
                    .postValue(ChartData(result.data, callback))
                is NetworkResult.Failure -> errorLiveData
                    .postValue(Unit)
            }
        }
    }

    fun setSymbol(symbol: Symbol) {
        chartIQHandler.setSymbol(symbol.value)
    }

    fun setDataMethod(dataMethod: DataMethod, symbol: Symbol) {
        chartIQHandler.setDataMethod(dataMethod, symbol.value)
    }

    fun fetchSavedSettings() {
        currentSymbol.value = applicationPrefs.getChartSymbol()
        chartInterval.value = applicationPrefs.getChartInterval()
        drawingTool.value = applicationPrefs.getDrawingTool()
    }

    fun setupInstrumentsList(item: DrawingToolItem): List<InstrumentItem> {
        val instrumentList = mutableListOf<InstrumentItem>()
        instrumentList.add(InstrumentItem(Instrument.DRAWING_TOOL, item.iconRes))
        with(drawingManager) {
            if (isSupportingFillColor(item.tool)) {
                instrumentList.add(
                    InstrumentItem(Instrument.FILL, R.drawable.ic_panel_fill)
                )
            }
            if (isSupportingLineColor(item.tool)) {
                instrumentList.add(
                    InstrumentItem(Instrument.COLOR, R.drawable.ic_panel_color)
                )
            }
            if (isSupportingLineType(item.tool)) {
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
            if (isSupportingSettings(item.tool)) {
                instrumentList.add(
                    InstrumentItem(Instrument.SETTINGS, R.drawable.ic_panel_options)
                )
            }
        }
        return instrumentList
    }

    fun enableDrawing(drawingTool: DrawingTool) {
        chartIQHandler.enableDrawing(drawingTool)
    }

    fun enableCrosshairs() {
        chartIQHandler.enableCrosshairs()
    }

    fun disableCrosshairs() {
        chartIQHandler.disableCrosshairs()
    }

    fun getHUDDetails() {
        chartIQHandler.getHUDDetails { hud ->
            crosshairHUD.value = hud
        }
    }

    fun updateFillColor(color: Int) {
        chartIQHandler.setDrawingParameter(
            DrawingParameter.FILL_COLOR.value,
            color.toHexStringWithHash()
        )
    }

    fun updateColor(color: Int) {
        chartIQHandler.setDrawingParameter(
            DrawingParameter.LINE_COLOR.value,
            color.toHexStringWithHash()
        )
    }

    fun updateLineType(lineType: LineType) {
        chartIQHandler.setDrawingParameter(DrawingParameter.LINE_TYPE.value, lineType.value)
    }

    fun updateLineWidth(lineWidth: Int) {
        chartIQHandler.setDrawingParameter(DrawingParameter.LINE_WIDTH.value, lineWidth.toString())
    }

    fun cloneDrawing() {
        chartIQHandler.cloneDrawing()
        resetInstruments(REFRESH_TIME_MILLIS)
    }

    fun deleteDrawing() {
        chartIQHandler.deleteDrawing()
        resetInstruments(REFRESH_TIME_MILLIS)
    }

    fun magnetDrawing() {
    }

    fun manageLayer(layer: ChartLayer) {
        chartIQHandler.manageLayer(layer)
        resetInstruments()
    }

    fun getDrawingToolParameters() {
        if (drawingTool.value != null) {
            chartIQHandler.getDrawingParameters(drawingTool.value!!) { parameters ->
                val gson = Gson()
                val jsonElement = gson.toJsonTree(parameters)
                this.parameters.value =
                    gson.fromJson(jsonElement, PanelDrawingToolParameters::class.java)
            }
        }
    }

    fun redo() {
        chartIQHandler.redo {}
    }

    fun undo() {
        chartIQHandler.undo {}
    }

    fun resetInstruments(delay: Long = REFRESH_IMMEDIATE_MILLIS) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(delay)
            withContext(Dispatchers.Main) {
                resetInstrumentsLiveData.value = Unit
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
        private const val REFRESH_TIME_MILLIS = 400L
        private const val REFRESH_IMMEDIATE_MILLIS = 0L
    }
}
