package com.chartiq.demo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chartiq.demo.util.Event

class MainViewModel : ViewModel() {

    val chartEvent = MutableLiveData<Event<ChartIQCommand>>()
}