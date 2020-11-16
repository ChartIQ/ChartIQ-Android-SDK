package com.chartiq.demo.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.Study

class MainViewModel : ViewModel() {

    val activeStudies = MutableLiveData<List<Study>>()

    fun fetchActiveStudyData(chartIQHandler: ChartIQHandler) {
        chartIQHandler.getActiveStudies { result ->
            activeStudies.postValue(result)
        }
    }
}