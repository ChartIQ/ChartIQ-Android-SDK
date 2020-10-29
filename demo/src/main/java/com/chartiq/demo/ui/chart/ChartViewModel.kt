package com.chartiq.demo.ui.chart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.R
import com.chartiq.demo.network.NetworkManager
import com.chartiq.demo.network.NetworkResult
import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolItem
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.panel.model.Instrument
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem
import com.chartiq.demo.ui.chart.searchsymbol.Symbol
import com.chartiq.demo.util.Event
import com.chartiq.sdk.DataSourceCallback
import com.chartiq.sdk.DrawingToolSettingsManager
import com.chartiq.sdk.model.DrawingTool
import com.chartiq.sdk.model.QuoteFeedParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChartViewModel(
    private val networkManager: NetworkManager,
    private val applicationPrefs: ApplicationPrefs
) : ViewModel(), OnSelectItemListener<InstrumentItem> {

    val currentSymbol = MutableLiveData<Event<Symbol>>()

    val chartInterval = MutableLiveData<Event<Interval>>()

    val drawingTool = MutableLiveData<Event<DrawingTool>>()

    val resultLiveData = MutableLiveData<ChartData>()

    val errorLiveData = MutableLiveData<Unit>()

    val panelItemSelect = MutableLiveData<Event<InstrumentItem>>()

    // TODO: 19.10.20 Review
    fun getDataFeed(params: QuoteFeedParams, callback: DataSourceCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = networkManager.fetchDataFeed(params)) {
                is NetworkResult.Success -> resultLiveData
                    .postValue(ChartData(result.data, callback))
                is NetworkResult.Failure -> errorLiveData
                    .postValue(Unit)
            }
        }
    }

    fun fetchSavedSettings() {
        currentSymbol.value = Event(applicationPrefs.getChartSymbol())
        chartInterval.value = Event(applicationPrefs.getChartInterval())
        drawingTool.value = Event(applicationPrefs.getDrawingTool())
    }

    fun setupInstrumentsList(item: DrawingToolItem): List<InstrumentItem> {
        val instrumentList = mutableListOf<InstrumentItem>()
        instrumentList.add(InstrumentItem(Instrument.DRAWING_TOOL, item.iconRes))
        with(DrawingToolSettingsManager) {
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

    class ChartViewModelFactory(
        private val argNetworkManager: NetworkManager,
        private val argApplicationPrefs: ApplicationPrefs
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(NetworkManager::class.java, ApplicationPrefs::class.java)
                .newInstance(argNetworkManager, argApplicationPrefs)
        }
    }

    override fun onSelected(item: InstrumentItem) {
        panelItemSelect.value = Event(item)
    }
}
