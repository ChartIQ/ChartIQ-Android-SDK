package com.chartiq.demo.ui.settings.chartstyle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChartStyleSelectionBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.common.FullscreenDialogFragment
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.charttype.ChartType

class ChartStyleSelectionFragment : FullscreenDialogFragment() {
    private lateinit var binding: FragmentChartStyleSelectionBinding

    private val selectedStyle: ChartTypeModel? by lazy {
        ChartStyleSelectionFragmentArgs.fromBundle(requireArguments()).selectedStyle
    }
    private val originalChartStyles: List<ChartTypeModel> by lazy {
        ChartType.values().map { it.toModel() } + AggregationChartType.values().map { it.toModel() }
    }
    private val optionsAdapter = SelectChartStyleAdapter()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartStyleSelectionBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                dismiss()
            }
            optionsAdapter.apply {
                items = originalChartStyles
                selectedValue = selectedStyle
                listener = object : SelectChartStyleAdapter.SelectChartStyleAdapterListener {
                    override fun onSelect(selectedValue: ChartTypeModel) {
                        (targetFragment as DialogFragmentListener).onChartStyleSelect(selectedValue)
                        dismiss()
                    }
                }
            }
            parametersRecyclerView.apply {
                adapter = optionsAdapter
                addItemDecoration(
                        LineItemDecoration(
                                context = requireContext(),
                                marginStart = context.resources.getDimensionPixelSize(R.dimen.drawing_tool_item_decorator_margin_start)
                        )
                )
                scrollToPosition(originalChartStyles.indexOf(selectedStyle))
            }
        }
    }

    companion object {
        fun getInstance(chartTypeModel: ChartTypeModel?): ChartStyleSelectionFragment {
            return ChartStyleSelectionFragment().apply {
                arguments = ChartStyleSelectionFragmentArgs.Builder(chartTypeModel).build().toBundle()
            }
        }
    }

    interface DialogFragmentListener {
        fun onChartStyleSelect(chartStyle: ChartTypeModel)
    }


}
