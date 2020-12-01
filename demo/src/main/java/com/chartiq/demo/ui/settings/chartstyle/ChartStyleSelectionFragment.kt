package com.chartiq.demo.ui.settings.chartstyle

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChartStyleSelectionBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.charttype.ChartType

class ChartStyleSelectionFragment : DialogFragment() {
    private lateinit var binding: FragmentChartStyleSelectionBinding

    private val selectedStyle: ChartTypeModel? by lazy {
        ChartStyleSelectionFragmentArgs.fromBundle(requireArguments()).selectedStyle
    }
    private val originalChartStyles: List<ChartTypeModel> by lazy {
        ChartType.values().map { it.toModel() } + AggregationChartType.values().map { it.toModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartStyleSelectionBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.attributes?.windowAnimations = R.style.FullScreenDialog
        }
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                dismiss()
            }
            val optionsAdapter = SelectChartStyleAdapter().apply {
                items = originalChartStyles
                selectedValue = selectedStyle
                listener = object : SelectChartStyleAdapter.SelectChartStyleAdapterListener {
                    override fun onSelect(selectedValue: ChartTypeModel) {
                        (targetFragment as DialogFragmentListener).onSelect(selectedValue)
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
        fun getInstance(bundle: Bundle): ChartStyleSelectionFragment {
            return ChartStyleSelectionFragment().apply {
                arguments = bundle
            }
        }
    }

    interface DialogFragmentListener {
        fun onSelect(chartStyle: ChartTypeModel)
    }


}
