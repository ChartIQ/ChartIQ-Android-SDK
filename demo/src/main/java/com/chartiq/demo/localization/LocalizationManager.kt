package com.chartiq.demo.localization

import android.content.Context
import android.view.View

interface LocalizationManager {

    fun init(context: Context)

    fun getTranslationFromValue(value: String, context: Context): String

    fun updateTranslationsForLocale(translations: RemoteTranslations, parentView: View)

    fun rewordUi(parentView: View)
}
