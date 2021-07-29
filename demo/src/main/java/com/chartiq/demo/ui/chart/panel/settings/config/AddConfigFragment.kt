package com.chartiq.demo.ui.chart.panel.settings.config

import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.chartiq.demo.databinding.FragmentChooseConfigBinding
import com.chartiq.demo.ui.common.FullscreenDialogFragment
import com.chartiq.demo.util.hideKeyboard

class AddConfigFragment : FullscreenDialogFragment() {

    private lateinit var binding: FragmentChooseConfigBinding

    private val supportsNegativeInput by lazy {
        requireArguments().getBoolean(ARG_SUPPORTS_NEGATIVE_INPUT)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.run {
                addButton.isEnabled = addConfigEditText.text?.isNotEmpty() ?: false
            }
        }

        override fun afterTextChanged(s: Editable?) {
            with(binding.addConfigEditText) {
                if (s.toString().startsWith(CHARACTER_MINUS) && !supportsNegativeInput) {
                    val input = s.toString().replace(CHARACTER_MINUS, "")
                    setText(input)
                    Selection.setSelection(text, text!!.length - 1)
                }
                if (!s.toString().endsWith(CHARACTER_PERCENTAGE)) {
                    val input = s.toString().replace(CHARACTER_PERCENTAGE, "")
                    setText(input + CHARACTER_PERCENTAGE)
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
        fun getInstance(supportsNegativeInput: Boolean): AddConfigFragment {
            val dialog = AddConfigFragment()
            dialog.arguments = bundleOf(
                ARG_SUPPORTS_NEGATIVE_INPUT to supportsNegativeInput
            )
            return dialog
        }

        private const val CHARACTER_PERCENTAGE = "%"
        private const val CHARACTER_MINUS = "-"
        private const val ARG_SUPPORTS_NEGATIVE_INPUT = "add.config.argument.supports.negative"
    }

    interface DialogFragmentListener {

        fun onCustomValueAdded(value: String)
    }
}
