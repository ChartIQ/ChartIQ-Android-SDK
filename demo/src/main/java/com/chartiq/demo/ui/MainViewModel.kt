package com.chartiq.demo.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.ui.study.StudyViewModel
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.Study

class MainViewModel(
    private val chartIQHandler: ChartIQ
) : ViewModel() {

    val activeStudies = MutableLiveData<List<Study>>()

    fun fetchActiveStudyData() {
        chartIQHandler.getActiveStudies { result ->
            activeStudies.value = result
        }
    }

    class MainViewModelFactory(private val argChartIQ: ChartIQ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(ChartIQ::class.java).newInstance(argChartIQ)
        }
    }
}
