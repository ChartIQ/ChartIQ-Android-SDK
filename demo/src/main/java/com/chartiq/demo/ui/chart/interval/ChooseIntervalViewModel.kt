package com.chartiq.demo.ui.chart.interval

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ui.chart.interval.list.IntervalItem
import com.chartiq.demo.ui.chart.interval.list.OnIntervalSelectListener
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.demo.util.Event

class ChooseIntervalViewModel(private val appPrefs: ApplicationPrefs) : ViewModel(),
    OnIntervalSelectListener {

    val intervalSelectEvent = MutableLiveData<Event<Interval>>()
    val chooseCustomIntervalEvent = MutableLiveData<Event<Unit>>()

    override fun onCustomIntervalSelect() {
        chooseCustomIntervalEvent.value = Event(Unit)
    }

    override fun onIntervalSelect(item: IntervalItem) {
        intervalSelectEvent.value = Event(Interval(item.duration, item.timeUnit))
    }

    fun setupList(intervalsList: List<Interval>): List<IntervalItem> {
        val selectedInterval = appPrefs.getChartInterval()
        // If selected interval is not in the default list then it's custom
        val customInterval = if (intervalsList.contains(selectedInterval)) {
            Interval(0, TimeUnit.SECOND)
        } else {
            Interval(selectedInterval.duration, selectedInterval.timeUnit)
        }
        val intervalItemsList = intervalsList
            .toMutableList()
            .apply {
                // Add custom interval item in the begging
                add(0, customInterval)
            }
            .map { IntervalItem(it.duration, it.timeUnit, it == selectedInterval) }
        return intervalItemsList
    }

    fun saveUserPreferences(interval: Interval) {
        appPrefs.saveChartInterval(interval)
    }

    class ChooseIntervalViewModelFactory(private val argAppPrefs: ApplicationPrefs) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(ApplicationPrefs::class.java).newInstance(argAppPrefs)
        }
    }
}
