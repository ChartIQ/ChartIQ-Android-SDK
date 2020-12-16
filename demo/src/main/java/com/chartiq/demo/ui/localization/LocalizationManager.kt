package com.chartiq.demo.ui.localization

import android.content.Context
import java.util.*

object LocalizationManager {

    fun getTranslationFromValue(value: String, context: Context): String {
        val identifier = value.toLowerCase(Locale.ENGLISH).replace(" ", "_")
        return if (context.resources.getIdentifier(identifier, "string", context.packageName) != 0) {
            context.resources.getString(context.resources.getIdentifier(identifier, "string", null))
        } else {
            value
        }
    }
}
