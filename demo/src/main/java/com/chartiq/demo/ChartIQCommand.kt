package com.chartiq.demo

import com.chartiq.sdk.model.Study

sealed class ChartIQCommand {

    object GetActiveStudies : ChartIQCommand()

    object GetAllStudies : ChartIQCommand()

    data class AddStudy(
        val study: Study
    ) : ChartIQCommand()

    data class DeleteStudy(
        val studyToDelete: Study
    ) : ChartIQCommand()
}