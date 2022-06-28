package com.chartiq.demo.ui.signal.addsignal

import com.chartiq.sdk.model.signal.Condition

data class ConditionItem(
    val condition: Condition,
    val title: String,
    val description: String
)