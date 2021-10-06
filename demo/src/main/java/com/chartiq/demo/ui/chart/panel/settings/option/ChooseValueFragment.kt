package com.chartiq.demo.ui.chart.panel.settings.option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChooseValueBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.chart.panel.settings.config.AddConfigFragment
import com.chartiq.demo.ui.common.FullscreenDialogFragment
import com.chartiq.demo.ui.common.optionpicker.OptionItem
import com.chartiq.demo.ui.common.optionpicker.OptionsAdapter

class ChooseValueFragment : FullscreenDialogFragment(), AddConfigFragment.DialogFragmentListener {

    private lateinit var binding: FragmentChooseValueBinding
    private lateinit var optionList: List<OptionItem>
    private val isMultipleSelect by lazy {
        requireArguments().getBoolean(ARG_IS_MULTIPLE_SELECTION)
    }
    private val supportsCustomValues by lazy {
        requireArguments().getBoolean(ARG_SUPPORTS_CUSTOM_VALUES)
    }
    private val supportsNegativeValues by lazy {
        requireArguments().getBoolean(ARG_SUPPORTS_NEGATIVE_VALUES)
    }
    private val parameter by lazy {
        requireArguments().getString(ARG_PARAM)
            ?: throw IllegalStateException("No drawing parameter was passed to the fragment")
    }
    private val valuesAdapter = OptionsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseValueBinding.inflate(inflater, container, false)

        setupViews()
        return binding.root
    }

    override fun onCustomValueAdded(value: String) {
        optionList = optionList.toMutableList().apply {
            add(OptionItem(value, true))
        }
        updateValues(optionList)
    }

    private fun setupViews() {
        with(binding) {
            optionList = requireArguments().getParcelableArrayList(ARG_OPTIONS)
                ?: throw IllegalStateException("No value options were passed to the fragment")

            valuesToolbar.apply {
                val title = getString(requireArguments().getInt(ARG_TITLE))
                setTitle(title)
                setNavigationOnClickListener {
                    if (isMultipleSelect) {
                        (targetFragment as DialogFragmentListener)
                            .onChooseValue(parameter, optionList)
                    }
                    dismiss()
                }
                if (supportsCustomValues) {
                    menu.findItem(R.id.add_custom_value).apply {
                        isVisible = true
                        setOnMenuItemClickListener {
                            navigateToCustomValue()
                            true
                        }
                    }
                }
            }
            valuesRecyclerView.apply {
                updateValues(optionList)
                valuesAdapter.listener = OnSelectItemListener { selectedOption ->
                    onValueSelected(selectedOption)
                }

                addItemDecoration(LineItemDecoration.Default(requireContext()))
                adapter = valuesAdapter
            }
        }
    }

    private fun navigateToCustomValue() {
        val dialog = AddConfigFragment.getInstance(supportsNegativeValues)
        dialog.setTargetFragment(this, REQUEST_CODE_SHOW_CUSTOM_VALUE)
        dialog.show(parentFragmentManager, null)
    }

    private fun onValueSelected(selectedOption: OptionItem) {
        val param = requireArguments().getString(ARG_PARAM) ?: ""

        if (isMultipleSelect) {
            optionList = optionList.map {
                if (it == selectedOption) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it.copy(isSelected = it.isSelected)
                }
            }
            updateValues(optionList)
        } else {
            val list = optionList.map { it.copy(isSelected = it == selectedOption) }
            (targetFragment as DialogFragmentListener).onChooseValue(param, list)
            dismiss()
        }
    }

    private fun updateValues(values: List<OptionItem>) {
        valuesAdapter.items = if (supportsNegativeValues) {
            values
        } else {
            values.filter { !it.value.startsWith("-") }
        }
    }

    companion object {
        fun getInstance(
            @StringRes title: Int,
            param: String,
            valueList: List<OptionItem>,
            isMultipleSelect: Boolean = false,
            supportsCustomValues: Boolean,
            supportsNegativeValues: Boolean
        ): ChooseValueFragment {
            val dialog = ChooseValueFragment()
            dialog.arguments = bundleOf(
                ARG_TITLE to title,
                ARG_PARAM to param,
                ARG_OPTIONS to valueList,
                ARG_IS_MULTIPLE_SELECTION to isMultipleSelect,
                ARG_SUPPORTS_CUSTOM_VALUES to supportsCustomValues,
                ARG_SUPPORTS_NEGATIVE_VALUES to supportsNegativeValues
            )
            return dialog
        }

        private const val ARG_TITLE = "choose.values.title"
        private const val ARG_PARAM = "choose.values.argument.parameter"
        private const val ARG_OPTIONS = "choose.values.argument.options.list"
        private const val ARG_IS_MULTIPLE_SELECTION = "choose.values.argument.is.multiple.selection"
        private const val ARG_SUPPORTS_CUSTOM_VALUES = "choose.values.argument.supports.custom.input"
        private const val ARG_SUPPORTS_NEGATIVE_VALUES = "choose.values.argument.supports.negative.values"

        private const val REQUEST_CODE_SHOW_CUSTOM_VALUE = 10210
    }

    interface DialogFragmentListener {

        fun onChooseValue(parameter: String, valuesList: List<OptionItem>)
    }
}
