package com.chartiq.demo.ui.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.use
import com.chartiq.demo.R

class TwoLineSettingsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var settingsTitleTextView: AppCompatTextView
    var settingsSubtitleTextView: AppCompatTextView

    var title: String? = null
        set(value) {
            settingsTitleTextView.text = value
            field = value
        }
    var subtitle: String? = null
        set(value) {
            settingsSubtitleTextView.text = value
            field = value
        }

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.widget_two_line_setting, this, true)
        settingsTitleTextView = view.findViewById(R.id.settingsTitleTextView)
        settingsSubtitleTextView = view.findViewById(R.id.settingsSubtitleTextView)
        attrs?.let { attributes ->
            context.obtainStyledAttributes(attributes, R.styleable.TwoLineSettingsView).use {
                val titleAttr = it.getString(R.styleable.TwoLineSettingsView_settingsTitle)
                val subtitleAttr = it.getString(R.styleable.TwoLineSettingsView_settingsSubtitle)
                title = titleAttr
                subtitle = subtitleAttr
            }
        }
    }
}
