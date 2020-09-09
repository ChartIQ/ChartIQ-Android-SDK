package com.chartiq.demo.ui.chart.interval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.R
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.chart.interval.list.IntervalListAdapter
import com.chartiq.demo.ui.chart.interval.list.IntervalProps
import com.chartiq.demo.ui.chart.interval.list.OnIntervalClickListener
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.interval.model.TimeUnit

class ChooseIntervalFragment : Fragment(), OnIntervalClickListener {

    private lateinit var appPrefs: ApplicationPrefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_choose_inteval, container, false)
        setupUI(root)
        return root
    }

    private fun setupUI(root: View) {
        appPrefs = ApplicationPrefs.Default(requireContext())
        val selectedInterval = appPrefs.getChartInterval()

        val intervalAdapter = IntervalListAdapter(
            INTERVAL_LIST.map { IntervalProps(it.value, it.timeUnit, it == selectedInterval) },
            this
        )

        root.findViewById<RecyclerView>(R.id.intervalsRecyclerView).apply {
            val itemDecoration = LineItemDecoration(
                context = context,
                marginStart = resources.getDimensionPixelSize(R.dimen.list_item_decorator_margin)
            )
            adapter = intervalAdapter
            addItemDecoration(itemDecoration)
        }

        root.findViewById<Toolbar>(R.id.toolbar).apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    override fun onCustomIntervalClick() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_chooseIntervalFragment_to_customIntervalFragment)
    }

    override fun onIntervalClick(interval: IntervalProps) {
        appPrefs.saveChartInterval(Interval(interval.value, interval.timeUnit))
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
