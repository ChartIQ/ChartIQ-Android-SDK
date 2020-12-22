package com.chartiq.demo.localization

import java.util.*

data class RemoteTranslations(
    val locale: Locale,
    val values: Map<String, String>
)
