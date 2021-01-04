package com.chartiq.demo.ui.common

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.chartiq.demo.R

abstract class FullscreenDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.attributes?.windowAnimations = R.style.FullScreenDialog
        }
    }
}
