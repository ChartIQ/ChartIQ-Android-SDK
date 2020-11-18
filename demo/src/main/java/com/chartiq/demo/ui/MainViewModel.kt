package com.chartiq.demo.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.Study

class MainViewModel : ViewModel() {

    val activeStudies = MutableLiveData<List<Study>>()

    fun fetchActiveStudyData(chartIQHandler: ChartIQ) {
        chartIQHandler.getActiveStudies { result ->
            activeStudies.postValue(result)
        }
    }
}
