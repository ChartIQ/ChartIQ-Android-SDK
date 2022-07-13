package com.chartiq.sdk.model.signal

import com.chartiq.sdk.OnReturnCallback
import com.chartiq.sdk.model.study.Study

interface ChartIQSignal {
    /**
     * Toggle signal [Signal]
     * @param signal A [Signal] to be toggled
     */
    fun toggleSignal(signal: Signal)

    /**
     * Gets a list of signals [Signal]
     * @param callback A callback to subscribe to to get a list of signals
     */
    fun getActiveSignals(callback: OnReturnCallback<List<Signal>>)

    /**
     * Removes a selected signal [Signal] from the list of active studies
     * @param signal A [Signal] to be deleted
     */
    fun removeSignal(signal: Signal)

    /**
     * Add signal [Signal]  to a list of active signals
     * @param signal A [Signal] to be added
     */
    fun addSignalStudy(name: String, callback: OnReturnCallback<Study>)

    /**
     * Add signal [Signal]  to a list of active signals
     * @param signal A [Signal] to be added
     */
    fun saveSignal(signal: Signal, editMode: Boolean)

}
