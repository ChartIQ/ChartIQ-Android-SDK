package com.chartiq.demo.ui.chart.interval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.databinding.FragmentChooseIntervalBinding
import com.chartiq.demo.ui.LineItemDecoration.Default
import com.chartiq.demo.ui.chart.interval.list.IntervalItem
import com.chartiq.demo.ui.chart.interval.list.IntervalListAdapter
import com.chartiq.demo.ui.chart.interval.list.OnIntervalSelectListener
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.common.FullscreenDialogFragment
import com.chartiq.sdk.model.TimeUnit

class ChooseIntervalFragment : FullscreenDialogFragment() {

    private val viewModel: ChooseIntervalViewModel by activityViewModels()
    private val localizationManager by lazy {
        (requireActivity().application as ChartIQApplication).localizationManager
    }
    private val onSelectIntervalListener = object : OnIntervalSelectListener {
        override fun onCustomIntervalSelect() {
            viewModel.onCustomIntervalSelect()
        }

        override fun onIntervalSelect(item: IntervalItem) {
            viewModel.onIntervalSelect(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentChooseIntervalBinding.inflate(inflater, container, false)
        setupViews(binding)
        return binding.root
    }

    private fun setupViews(binding: FragmentChooseIntervalBinding) {
        viewModel.intervalSelectedEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                viewModel.interval.value?.let { interval ->
                    (targetFragment as DialogFragmentListener).onChooseInterval(interval)
                }
                dismiss()
            }
        }
        viewModel.chooseCustomIntervalEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                navigateToChooseCustomInterval()
            }
        }

        val intervalAdapter = IntervalListAdapter().apply {
            localizationManager = this@ChooseIntervalFragment.localizationManager
            items = viewModel.setupList(
                DEFAULT_INTERVAL_LIST,
                requireArguments().getParcelable(ARG_INTERVAL)
            )
            onSelectIntervalListener = this@ChooseIntervalFragment.onSelectIntervalListener
        }
        with(binding) {
            intervalsRecyclerView.apply {
                adapter = intervalAdapter
                addItemDecoration(Default(requireContext()))
            }

            toolbar.setNavigationOnClickListener {
                dismiss()
            }
        }
    }

    private fun navigateToChooseCustomInterval() {
        CustomIntervalFragment
            .getInstance()
            .show(parentFragmentManager, null)
    }

    companion object {

        fun getInstance(interval: Interval): ChooseIntervalFragment {
            val dialog = ChooseIntervalFragment()
            dialog.arguments = bundleOf(
                ARG_INTERVAL to interval
            )
            return dialog
        }

        private const val ARG_INTERVAL = "choose.interval.argument.interval"

        private val DEFAULT_INTERVAL_LIST = listOf(
            Interval(1, TimeUnit.DAY),
            Interval(1, TimeUnit.WEEK),
            Interval(1, TimeUnit.MONTH),
            Interval(1, TimeUnit.MINUTE),
            Interval(5, TimeUnit.MINUTE),
            Interval(10, TimeUnit.MINUTE),
            Interval(15, TimeUnit.MINUTE),
            Interval(30, TimeUnit.MINUTE),
            Interval(1, TimeUnit.HOUR),
            Interval(4, TimeUnit.HOUR),
            Interval(30, TimeUnit.SECOND),
        )
    }

    interface DialogFragmentListener {

        fun onChooseInterval(interval: Interval)
    }
}
