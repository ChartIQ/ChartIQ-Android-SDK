package com.chartiq.demo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chartiq.demo.util.Event
import com.chartiq.sdk.model.Study

class MainViewModel : ViewModel() {
    val activeStudies = MutableLiveData<List<Study>>(emptyList())

    val chartEvent = MutableLiveData<Event<ChartIQCommand>>()
}