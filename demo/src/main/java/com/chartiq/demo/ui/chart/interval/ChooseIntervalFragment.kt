package com.chartiq.demo.ui.chart.interval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChooseIntervalBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.chart.interval.list.IntervalItem
import com.chartiq.demo.ui.chart.interval.list.IntervalListAdapter
import com.chartiq.demo.ui.chart.interval.list.OnIntervalSelectListener
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.interval.model.TimeUnit

class ChooseIntervalFragment : Fragment() {

    private val viewModel: ChooseIntervalViewModel by activityViewModels(factoryProducer = {
        ChooseIntervalViewModel.ChooseIntervalViewModelFactory(
            ApplicationPrefs.Default(requireContext())
        )
    })
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
                findNavController().navigateUp()
            }
        }
        viewModel.chooseCustomIntervalEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                navigateToChooseCustomInterval()
            }
        }

        val intervalAdapter = IntervalListAdapter()
        intervalAdapter.items = viewModel.setupList(DEFAULT_INTERVAL_LIST)
        intervalAdapter.onSelectIntervalListener = onSelectIntervalListener

        with(binding) {
            intervalsRecyclerView.apply {
                adapter = intervalAdapter
                addItemDecoration(LineItemDecoration.Default(context))
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun navigateToChooseCustomInterval() {
        NavHostFragment.findNavController(this@ChooseIntervalFragment)
            .navigate(R.id.action_chooseIntervalFragment_to_customIntervalFragment)
    }

    companion object {
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
}
