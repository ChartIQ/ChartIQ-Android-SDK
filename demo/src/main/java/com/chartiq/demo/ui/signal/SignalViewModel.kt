package com.chartiq.demo.ui.signal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.ChartIQ

import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.signal.Signal
import com.chartiq.sdk.model.study.Study

class SignalViewModel(private val chartIQ: ChartIQ) : ViewModel() {

    fun deleteSignal(signalToDelete: Signal) {
        chartIQ.removeSignal(signalToDelete)
    }

    fun toggledSignal(signal: Signal, checked: Boolean) {
        chartIQ.toggleSignal(signal)
    }

    class ViewModelFactory(private val chartIQ: ChartIQ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java)
                .newInstance(chartIQ)
        }
    }
}
