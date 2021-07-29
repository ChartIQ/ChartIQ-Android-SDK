package com.chartiq.demo.ui.chart

sealed class ChartViewState {
    sealed class Simple : ChartViewState() {
        data class Portrait(
            val menuExpanded: Boolean
        ) : Simple()

        object Landscape : Simple()
    }

    data class FullView(
        val afterRotation: Boolean
    ) : ChartViewState()
}