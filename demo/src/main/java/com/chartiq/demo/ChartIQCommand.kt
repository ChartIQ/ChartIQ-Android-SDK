package com.chartiq.demo

import com.chartiq.sdk.model.Study

sealed class ChartIQCommand {
    data class AddStudy(
        val study: Study
    ) : ChartIQCommand()
}