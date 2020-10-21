package com.chartiq.demo.ui.chart.interval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChooseIntervalBinding
import com.chartiq.demo.ui.chart.interval.list.IntervalListAdapter
import com.chartiq.demo.ui.chart.interval.list.IntervalProps
import com.chartiq.demo.ui.chart.interval.list.OnIntervalClickListener
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.interval.model.TimeUnit

class ChooseIntervalFragment : Fragment(), OnIntervalClickListener {

    private val appPrefs: ApplicationPrefs by lazy {
        ApplicationPrefs.Default(requireContext())
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
        val selectedInterval = appPrefs.getChartInterval()

        val intervalAdapter = IntervalListAdapter(
            INTERVAL_LIST.map { IntervalProps(it.duration, it.timeUnit, it == selectedInterval) },
            this
        )

        with(binding) {
            intervalsRecyclerView.apply {
                adapter = intervalAdapter
                addItemDecoration(com.chartiq.demo.ui.LineItemDecoration.Default(context))
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    override fun onCustomIntervalClick() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_chooseIntervalFragment_to_customIntervalFragment)
    }

    override fun onIntervalClick(interval: IntervalProps) {
        appPrefs.saveChartInterval(Interval(interval.duration, interval.timeUnit))
    }

    companion object {
        private val INTERVAL_LIST = listOf(
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
