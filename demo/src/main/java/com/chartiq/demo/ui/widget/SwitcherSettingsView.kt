package com.chartiq.demo.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import com.chartiq.demo.R
import kotlinx.android.synthetic.main.widget_switcher_setting.view.*

class SwitcherSettingsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var onCheckChangeListener: (Boolean) -> Unit = { }

    var title: String? = null
        set(value) {
            parameterTextView.text = value
            field = value
        }
    var isChecked: Boolean = false
        set(value) {
            parameterSwitch.setOnCheckedChangeListener(null)
            parameterSwitch.isChecked = value
            parameterSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                onCheckChangeListener(isChecked)
            }
            field = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_switcher_setting, this, true)
        attrs?.let { attributes ->
            context.obtainStyledAttributes(attributes, R.styleable.SwitcherSettingsView).use {
                val titleAttr = it.getString(R.styleable.SwitcherSettingsView_settingsSwitcherTitle)
                val isCheckedAttr = it.getBoolean(R.styleable.SwitcherSettingsView_settingsSwitcherChecked, false)
                title = titleAttr
                isChecked = isCheckedAttr
            }
        }
    }
}
