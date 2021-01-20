package com.chartiq.demo.ui.chart.panel.settings.config

import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chartiq.demo.databinding.FragmentChooseConfigBinding
import com.chartiq.demo.ui.common.FullscreenDialogFragment
import com.chartiq.demo.util.hideKeyboard

class AddConfigFragment : FullscreenDialogFragment() {

    private lateinit var binding: FragmentChooseConfigBinding

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.run {
                addButton.isEnabled = addConfigEditText.text?.isNotEmpty() ?: false
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (!s.toString().endsWith(PERCENTAGE)) {
                with(binding.addConfigEditText) {
                    val input = s.toString().replace(PERCENTAGE, "")
                    setText(input + PERCENTAGE)
                    Selection.setSelection(text, text!!.length - 1)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseConfigBinding.inflate(inflater, container, false)

        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                dismiss()
            }
            addButton.setOnClickListener {
                requireContext().hideKeyboard(view?.windowToken)
                (targetFragment as DialogFragmentListener).onCustomValueAdded(addConfigEditText.text.toString())
                dismiss()
            }
            addConfigEditText.addTextChangedListener(textWatcher)
        }
    }

    companion object {
        fun getInstance(): AddConfigFragment {
            return AddConfigFragment()
        }

        private const val PERCENTAGE = "%"
    }

    interface DialogFragmentListener {

        fun onCustomValueAdded(value: String)
    }
}
