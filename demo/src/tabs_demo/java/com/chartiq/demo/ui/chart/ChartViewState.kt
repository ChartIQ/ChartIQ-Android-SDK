package com.chartiq.demo.ui.chart

sealed class ChartViewState {
    sealed class Simple : ChartViewState() {
        object Portrait : Simple()
        object Landscape : Simple()
    }

    data class FullView(
        val afterRotation: Boolean
    ) : ChartViewState()
}