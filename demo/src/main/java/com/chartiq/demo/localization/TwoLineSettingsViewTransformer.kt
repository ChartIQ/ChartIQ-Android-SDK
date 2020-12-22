package com.chartiq.demo.localization

import com.chartiq.demo.ui.widget.TwoLineSettingsView
import dev.b3nedikt.reword.transformer.AbstractViewTransformer

class TwoLineSettingsViewTransformer : AbstractViewTransformer<TwoLineSettingsView>() {
    override val supportedAttributes: Set<String> = setOf(
        settingsTitle,
        settingsSubtitle
    )

    override val viewType: Class<TwoLineSettingsView> = TwoLineSettingsView::class.java

    override fun TwoLineSettingsView.transform(attrs: Map<String, Int>) {
        attrs.forEach { entry ->
            when (entry.key) {
                settingsTitle -> {
                    title = resources.getString(entry.value)
                }
                settingsSubtitle -> {
                    subtitle = resources.getString(entry.value)
                }
            }
        }
    }

    companion object {
        private const val settingsTitle = "settingsTitle"
        private const val settingsSubtitle = "settingsSubtitle"
    }
}
