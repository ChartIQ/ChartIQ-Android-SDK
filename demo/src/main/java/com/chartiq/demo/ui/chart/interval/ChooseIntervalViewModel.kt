package com.chartiq.demo.ui.chart.interval

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chartiq.demo.ui.chart.interval.list.IntervalItem
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.util.Event
import com.chartiq.sdk.model.TimeUnit

class ChooseIntervalViewModel : ViewModel() {

    val intervalSelectedEvent = MutableLiveData<Event<Unit>>()
    val chooseCustomIntervalEvent = MutableLiveData<Event<Unit>>()
    val interval = MutableLiveData<Interval>()

    fun onCustomIntervalSelect() {
        chooseCustomIntervalEvent.value = Event(Unit)
    }

    fun onIntervalSelect(item: IntervalItem) {
        interval.value = Interval(item.duration, item.timeUnit)
        intervalSelectedEvent.value = Event(Unit)
    }

    fun setupList(intervalsList: List<Interval>, selectedInterval: Interval?): List<IntervalItem> {
        // If selected interval is not in the default list then it's custom
        val customInterval = if (selectedInterval == null || intervalsList.contains(selectedInterval)) {
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
}
