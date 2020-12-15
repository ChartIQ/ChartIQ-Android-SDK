package com.chartiq.demo.ui.localization

import java.util.*

data class RemoteTranslations(
    val locale: Locale,
    val values: Map<String, String>
)