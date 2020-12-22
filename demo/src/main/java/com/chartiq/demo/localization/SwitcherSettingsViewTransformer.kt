package com.chartiq.demo.localization

import com.chartiq.demo.ui.widget.SwitcherSettingsView
import dev.b3nedikt.reword.transformer.AbstractViewTransformer

class SwitcherSettingsViewTransformer : AbstractViewTransformer<SwitcherSettingsView>() {

    override val supportedAttributes: Set<String> =
        setOf(settingsSwitcherChecked, settingsSwitcherTitle)

    override val viewType: Class<SwitcherSettingsView> = SwitcherSettingsView::class.java

    override fun SwitcherSettingsView.transform(attrs: Map<String, Int>) {
        attrs.forEach { entry ->
            when (entry.key) {
                settingsSwitcherTitle -> {
                    title = resources.getString(entry.value)
                }
            }
        }
    }

    companion object {
        private const val settingsSwitcherTitle = "settingsSwitcherTitle"
        private const val settingsSwitcherChecked = "settingsSwitcherChecked"
    }
}
