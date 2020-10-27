package com.chartiq.demo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chartiq.demo.util.Event
import com.chartiq.sdk.model.Study

class MainViewModel : ViewModel() {

    fun fetchData() {
        chartEvent.postValue(Event(ChartIQCommand.GetActiveStudies))
    }

    val activeStudies = MutableLiveData<List<Study>>(emptyList())

    val allStudies = MutableLiveData<List<Study>>(emptyList())

    val chartEvent = MutableLiveData<Event<ChartIQCommand>>()
}