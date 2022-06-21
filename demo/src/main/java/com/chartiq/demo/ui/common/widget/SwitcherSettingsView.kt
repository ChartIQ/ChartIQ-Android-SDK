package com.chartiq.demo.ui.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import com.chartiq.demo.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SwitcherSettingsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var onCheckChangeListener: (Boolean) -> Unit = { }

    private var parameterTextView: AppCompatTextView
    private var parameterSwitch: SwitchMaterial

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
        val view =
            LayoutInflater.from(context).inflate(R.layout.widget_switcher_setting, this, true)
        parameterTextView = view.findViewById(R.id.parameterTextView)
        parameterSwitch = view.findViewById(R.id.parameterSwitch)
        attrs?.let { attributes ->
            context.obtainStyledAttributes(attributes, R.styleable.SwitcherSettingsView).use {
                val titleAttr = it.getString(R.styleable.SwitcherSettingsView_settingsSwitcherTitle)
                val isCheckedAttr =
                    it.getBoolean(R.styleable.SwitcherSettingsView_settingsSwitcherChecked, false)
                title = titleAttr
                isChecked = isCheckedAttr
            }
        }
    }
}
