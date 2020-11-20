package com.chartiq.demo.ui.chart.panel.settings.option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.chartiq.demo.databinding.FragmentChooseValueBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.chart.panel.settings.line.ChooseLineFragment
import com.chartiq.demo.ui.common.FullscreenDialogFragment
import com.chartiq.demo.ui.common.optionpicker.OptionItem
import com.chartiq.demo.ui.common.optionpicker.OptionsAdapter

class ChooseValueFragment : FullscreenDialogFragment() {

    private lateinit var binding: FragmentChooseValueBinding
    private lateinit var optionList: List<OptionItem>

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
            val isMultipleSelect = arguments?.getBoolean(ARG_IS_MULTIPLE_SELECTION) ?: false
            val parameter = arguments?.getString(ARG_PARAM)
                ?: throw IllegalStateException("No drawing parameter was passed to the fragment")
            optionList = arguments?.getParcelableArrayList(ARG_OPTIONS)
                ?: throw IllegalStateException("No value options were passed to the fragment")

            valuesToolbar.setNavigationOnClickListener {
                if (isMultipleSelect) {
                    (targetFragment as DialogFragmentListener)
                        .onChooseValue(parameter, optionList, true)
                }
                dismiss()
            }
            valuesRecyclerView.apply {
                val valuesAdapter = OptionsAdapter()
                valuesAdapter.items = optionList
                valuesAdapter.listener = OnSelectItemListener { selectedOption ->
                    val param = arguments?.getString(ARG_PARAM) ?: ""

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
                        optionList = optionList.map { it.copy(isSelected = it == selectedOption) }
                        valuesAdapter.items = optionList

                        (targetFragment as DialogFragmentListener)
                            .onChooseValue(param, optionList, false)
                        dismiss()
                    }
                }

                addItemDecoration(LineItemDecoration.Default(requireContext()))
                adapter = valuesAdapter
            }
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
