package com.chartiq.demo.ui.chart.panel.settings.option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.chartiq.demo.databinding.FragmentChooseValueBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.common.FullscreenDialogFragment
import com.chartiq.demo.ui.common.optionpicker.OptionItem
import com.chartiq.demo.ui.common.optionpicker.OptionsAdapter

class ChooseValueFragment : FullscreenDialogFragment() {

    private lateinit var binding: FragmentChooseValueBinding
    private lateinit var optionList: List<OptionItem>
    private val isMultipleSelect by lazy {
        requireArguments().getBoolean(ARG_IS_MULTIPLE_SELECTION)
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

    private fun setupViews() {
        with(binding) {
            optionList = requireArguments().getParcelableArrayList(ARG_OPTIONS)
                ?: throw IllegalStateException("No value options were passed to the fragment")

            valuesToolbar.setNavigationOnClickListener {
                if (isMultipleSelect) {
                    (targetFragment as DialogFragmentListener)
                        .onChooseValue(parameter, optionList, true)
                }
                dismiss()
            }
            valuesRecyclerView.apply {
                valuesAdapter.items = optionList
                valuesAdapter.listener = OnSelectItemListener { selectedOption ->
                    onValueSelected(selectedOption)
                }

                addItemDecoration(LineItemDecoration.Default(requireContext()))
                adapter = valuesAdapter
            }
        }
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
            valuesAdapter.items = optionList
        } else {
            val list = optionList.map { it.copy(isSelected = it == selectedOption) }
            (targetFragment as DialogFragmentListener).onChooseValue(param, list, false)
            dismiss()
        }
    }

    companion object {
        fun getInstance(
            param: String,
            valueList: List<OptionItem>,
            isMultipleSelect: Boolean = false
        ): ChooseValueFragment {
            val dialog = ChooseValueFragment()
            dialog.arguments = bundleOf(
                ARG_PARAM to param,
                ARG_OPTIONS to valueList,
                ARG_IS_MULTIPLE_SELECTION to isMultipleSelect
            )
            return dialog
        }

        private const val ARG_PARAM = "choose.values.argument.parameter"
        private const val ARG_OPTIONS = "choose.values.argument.options.list"
        private const val ARG_IS_MULTIPLE_SELECTION = "choose.values.argument.is.multiple.selection"
    }

    interface DialogFragmentListener {

        fun onChooseValue(
            parameter: String,
            valuesList: List<OptionItem>,
            isMultipleSelect: Boolean
        )
    }
}
