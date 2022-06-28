package com.chartiq.demo.ui.signal.addsignal

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.localization.LocalizationManager
import com.chartiq.demo.util.combineLatest
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.signal.Condition
import com.chartiq.sdk.model.signal.SignalJoiner
import com.chartiq.sdk.model.signal.SignalShape
import com.chartiq.sdk.model.signal.SignalSize
import com.chartiq.sdk.model.signal.SignalTemp
import com.chartiq.sdk.model.study.Study
import java.util.*

class AddSignalViewModel(
    private val chartIQ: ChartIQ,
    private val localizationManager: LocalizationManager,
    private val context: Context
) : ViewModel() {

    private val originalStudies = MutableLiveData<List<Study>>(emptyList())

    private val query = MutableLiveData("")

    val tempStudy = MutableLiveData<Study?>()
    val selectedStudy = MutableLiveData<Study?>()
    val signalJoiner = MutableLiveData(SignalJoiner.OR)
    val name = MutableLiveData("")
    val description = MutableLiveData("")

    val listOfConditions = MutableLiveData<List<Condition>>(emptyList())

    val isSaveAvailable = MediatorLiveData<Boolean>()
    val isAddConditionAvailable = MediatorLiveData<Boolean>()

    val filteredStudies =
        Transformations.map(originalStudies.combineLatest(query)) { (list, query) ->
            val filtered = list
                .filter {
                    localizationManager.getTranslationFromValue(it.name, context)
                        .lowercase(Locale.getDefault())
                        .contains(query.lowercase(Locale.getDefault()))
                }
            filtered
        }

    init {
        isAddConditionAvailable.addSource(selectedStudy) { study ->
            isAddConditionAvailable.value = study != null
        }
        isSaveAvailable.addSource(listOfConditions) {
            checkIsSaveAvailable()
        }
        isSaveAvailable.addSource(name) {
            checkIsSaveAvailable()
        }
        selectedStudy.value = null
        chartIQ.getStudyList { list ->
            originalStudies.postValue(list.sortedBy { it.name })
        }
    }

    private fun checkIsSaveAvailable() {
        isSaveAvailable.value =
            isAddConditionAvailable.value == true && !listOfConditions.value.isNullOrEmpty() && name.value?.isNotBlank() == true
    }

    fun onStudySelect(study: Study) {
        tempStudy.value = study
    }

    fun onStudyApproved() {
        chartIQ.addSignalStudy(tempStudy.value!!.shortName) { study ->
            selectedStudy.value = study
        }
    }

    fun onNewQuery(value: String) {
        query.postValue(value)
    }

    fun addCondition(condition: Condition) {
        listOfConditions.value = listOfConditions.value?.toMutableList()?.apply { add(condition) }
    }

    fun deleteCondition(condition: Condition) {
        listOfConditions.value = listOfConditions.value?.toMutableList()?.apply {
            remove(condition)
        }
    }

    fun onJoinerChanged(signalJoiner: SignalJoiner) {
        this.signalJoiner.value = signalJoiner
    }

    fun onNameChanged(name: String) {
        this.name.value = name
    }

    fun onClearStudy() {
        tempStudy.value = null
    }

    fun saveSignal() {
        chartIQ.addSignal(
            selectedStudy.value?.shortName ?: "",
            SignalTemp(
                name = name.value!!,
                conditions = listOfConditions.value ?: emptyList(),
                signalJoiner = signalJoiner.value ?: SignalJoiner.OR,
                description = description.value ?: "",
                shape = SignalShape.CIRCLE,
                size = SignalSize.M,
                label = ""
            ),
            false
        )
    }

    fun onDescriptionChanged(desc: String) {
        description.value = desc
    }

    fun clearViewModel() {
        listOfConditions.value = emptyList()
        query.value = ""
        tempStudy.value = null
        selectedStudy.value = null
        signalJoiner.value = SignalJoiner.OR
        name.value = ""
        description.value = ""
    }

    fun onBackPressed() {
        selectedStudy.value?.let { chartIQ.removeStudy(it) }
        clearViewModel()
    }

    class ViewModelFactory(
        private val chartIQ: ChartIQ,
        private val localizationManager: LocalizationManager,
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(
                    ChartIQ::class.java,
                    LocalizationManager::class.java,
                    Context::class.java
                )
                .newInstance(chartIQ, localizationManager, context)
        }
    }

}