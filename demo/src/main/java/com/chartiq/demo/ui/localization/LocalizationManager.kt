package com.chartiq.demo.ui.localization

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import com.chartiq.demo.R
import dev.b3nedikt.restring.Restring
import dev.b3nedikt.reword.Reword
import java.util.*

object LocalizationManager {

    fun getTranslationFromValue(value: String, context: Context): String {
        val identifier = value.toLowerCase(Locale.ENGLISH).replace(ORIGINAL_STRING_DELIMITER, NEW_STRING_DELIMITER)
        return if (context.resources.getIdentifier(identifier, "string", context.packageName) != 0) {
            context.resources.getString(context.resources.getIdentifier(identifier, "string", null))
        } else {
            value
        }
    }

    fun updateTranslationsForLocale(translations: RemoteTranslations, context: Context, parentView: View) {
        val currentStrings: List<ResourceTranslationItem> = getDefaultStrings(context)
        val newTranslationItems: List<ResourceTranslationItem> = currentStrings.map {
            ResourceTranslationItem(
                it.resourceKey,
                translations.values[it.resourceValue] ?: it.resourceValue
            )
        }
        val extraStrings =
            translations.values.filter { translation -> translation.key !in currentStrings.map { it.resourceValue } }

        val newExtraTranslations = extraStrings.map { (key, value) ->
            ResourceTranslationItem(
                key.toLowerCase(Locale.ENGLISH).replace(ORIGINAL_STRING_DELIMITER, NEW_STRING_DELIMITER),
                value
            )
        }
        Restring.putStrings(
            translations.locale,
            (newTranslationItems + newExtraTranslations).map { it.resourceKey to it.resourceValue }.toMap()
        )

        Restring.locale = translations.locale
        rewordUi(parentView)
    }

    fun rewordUi(parentView: View) {
        Reword.reword(parentView)
    }

    private fun getDefaultStrings(context: Context): List<ResourceTranslationItem> {
        return R.string::class.java.fields
            .map { field ->
                val resValue = getDefaultLocalizedResources(context).getString(field.getInt(null))
                ResourceTranslationItem(resourceKey = field.name, resourceValue = resValue)
            }
    }

    private fun getDefaultLocalizedResources(context: Context): Resources {
        val conf = Configuration(context.resources.configuration)
        conf.setLocale(Locale.ENGLISH)
        val localizedContext: Context = context.createConfigurationContext(conf)
        return localizedContext.resources
    }

    fun wrapContext(context: Context): Context {
        return Restring.wrapContext(context)
    }

    private const val ORIGINAL_STRING_DELIMITER = " "
    private const val NEW_STRING_DELIMITER = "_"
}
