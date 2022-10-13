package com.chartiq.demo.ui.signal.addsignal

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.localization.LocalizationManager
import com.chartiq.demo.util.Event
import com.chartiq.demo.util.combineLatest
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.signal.Condition
import com.chartiq.sdk.model.signal.Signal
import com.chartiq.sdk.model.signal.SignalJoiner
import com.chartiq.sdk.model.study.Study
import com.chartiq.sdk.model.study.StudyParameter
import com.chartiq.sdk.model.study.StudyParameterType
import com.chartiq.sdk.model.study.StudySimplified
import java.util.*

class AddSignalViewModel(
    private val chartIQ: ChartIQ,
    private val localizationManager: LocalizationManager,
    private val context: Context
) : ViewModel() {

    private val originalStudies = MutableLiveData<List<Study>>(emptyList())

    private val query = MutableLiveData("")
    val backEvent = MutableLiveData<Event<Unit>>()

    private val tempStudy = MutableLiveData<Study?>()
    val selectedStudy = MutableLiveData<Study?>()
    val signalJoiner = MutableLiveData(SignalJoiner.OR)
    val name = MutableLiveData("")
    val description = MutableLiveData("")
    val editType = MutableLiveData(SignalEditType.NEW_SIGNAL)

    val defaultColor = MediatorLiveData<Int>().apply {
        value = DEF_COLOR
    }
    val listOfConditions = MutableLiveData<List<ConditionItem>>(emptyList())

    val isSaveAvailable = MediatorLiveData<Boolean>()
    val isAddConditionAvailable = MediatorLiveData<Boolean>()
    val isSaveStudyAvailable = MutableLiveData(false)
    private val isFirstOpen = MutableLiveData(true)

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
            if (study != null) {
                chartIQ.getStudyParameters(study, StudyParameterType.Outputs) {
                    (it.firstOrNull() as? StudyParameter.Color)?.value?.let { color ->
                        defaultColor.value = Color.parseColor(color)
                    }
                }
            }
        }
        isSaveAvailable.addSource(listOfConditions) {
            checkIsSaveAvailable()
        }
        isSaveAvailable.addSource(name) {
            checkIsSaveAvailable()
        }
        isSaveAvailable.addSource(isAddConditionAvailable) {
            checkIsSaveAvailable()
        }
        selectedStudy.value = null
        chartIQ.getStudyList { list ->
            val studies = list.filter { study ->
                !study.signalIQExclude
            }
            originalStudies.postValue(studies.sortedBy { it.name })
        }
    }

    private fun checkIsSaveAvailable() {
        isSaveAvailable.value =
            isAddConditionAvailable.value == true && !listOfConditions.value.isNullOrEmpty() && name.value?.isNotBlank() == true
    }

    fun onStudySelect(study: Study) {
        isSaveStudyAvailable.value = true
        tempStudy.value = study
    }

    fun onStudyApproved() {
        selectedStudy.value?.let { chartIQ.removeStudy(it) }
        query.value = ""
        listOfConditions.value = emptyList()
        chartIQ.addSignalStudy(tempStudy.value!!.shortName) { study ->
            selectedStudy.value = study
            backEvent.value = Event(Unit)
        }
        onClearStudy()
    }

    fun onNewQuery(value: String) {
        query.postValue(value)
    }

    fun addCondition(condition: ConditionItem) {
        if (listOfConditions.value?.firstOrNull { it.UUID == condition.UUID } != null) {
            listOfConditions.value = listOfConditions.value?.toMutableList()?.apply {
                this[this.indexOfFirst { it.UUID == condition.UUID }] = condition
            }
        } else {
            listOfConditions.value = listOfConditions.value?.toMutableList()?.apply {
                add(condition)
            }
        }
        listOfConditions.value = listOfConditions.value?.sortedBy { it.title }
    }

    fun deleteCondition(condition: Condition) {
        listOfConditions.value = listOfConditions.value?.toMutableList()?.apply {
            remove(this.firstOrNull { it.condition == condition })
        }
    }

    fun onJoinerChanged(signalJoiner: SignalJoiner) {
        this.signalJoiner.value = signalJoiner
    }

    fun onNameChanged(name: String) {
        if (this.name.value != name) {
            this.name.value = name
        }
    }

    fun onClearStudy() {
        isSaveStudyAvailable.value = false
        tempStudy.value = null
    }

    fun saveSignal() {
        chartIQ.saveSignal(
            Signal(
                uniqueId = "",
                name = name.value!!,
                conditions = listOfConditions.value?.map { it.condition } ?: emptyList(),
                joiner = signalJoiner.value ?: SignalJoiner.OR,
                description = description.value ?: "",
                study = selectedStudy.value!!,
                disabled = false
            ),
            when (editType.value) {
                SignalEditType.NEW_SIGNAL -> false
                SignalEditType.EDIT_SIGNAL -> true
                null -> false
            }
        )
        clearViewModel()
    }

    fun onDescriptionChanged(desc: String) {
        if (description.value != desc) {
            description.value = desc
        }
    }

    private fun clearViewModel() {
        listOfConditions.value = emptyList()
        query.value = ""
        tempStudy.value = null
        selectedStudy.value = null
        signalJoiner.value = SignalJoiner.OR
        name.value = ""
        description.value = ""
        editType.value = SignalEditType.NEW_SIGNAL
        isFirstOpen.value = true
    }

    fun onBackPressed() {
        when (editType.value) {
            SignalEditType.NEW_SIGNAL -> {
                selectedStudy.value?.let { chartIQ.removeStudy(it) }
            }
            SignalEditType.EDIT_SIGNAL -> {
                editType.value = SignalEditType.NEW_SIGNAL
            }
            null -> {}
        }
        clearViewModel()
    }

    fun setSignal(signal: Signal) {
        if (isFirstOpen.value == true) {
            isFirstOpen.value = false
            chartIQ.getStudyParameters(
                signal.study,
                StudyParameterType.Outputs
            ) { list: List<StudyParameter> ->
                editType.value = SignalEditType.EDIT_SIGNAL
                selectedStudy.value = signal.study
                name.value = signal.name
                description.value = signal.description
                signalJoiner.value = signal.joiner
                listOfConditions.value = signal.conditions.map { condition ->
                    val color = if (condition.markerOption.color != null) {
                        condition.markerOption.color!!
                    } else {
                        val name =
                            condition.leftIndicator.substringBefore(ZERO_WIDTH_NON_JOINER).trim()
                        (list.firstOrNull {
                            (it as? StudyParameter.Color)?.name == name
                        } as? StudyParameter.Color)?.value ?: (list.firstOrNull() as? StudyParameter.Color)?.value
                    }
                    ConditionItem(
                        condition = condition,
                        title = "",
                        description = "",
                        displayedColor = color ?: ""
                    )
                }
                isSaveAvailable.value = true
            }
        }
    }

    fun onStudyEdited(study: StudySimplified?) {
        study?.let { studySimplified ->
            listOfConditions.value = listOfConditions.value?.map { item ->
                item.copy(
                    condition = item.condition.copy(
                        leftIndicator = item.condition.leftIndicator.replace(
                            selectedStudy.value!!.shortName,
                            studySimplified.studyName
                        ),
                        rightIndicator = item.condition.rightIndicator?.replace(
                            selectedStudy.value!!.shortName,
                            studySimplified.studyName
                        )
                    )
                )
            }
            selectedStudy.value = selectedStudy.value!!.copy(
                name = studySimplified.studyName,
                shortName = studySimplified.studyName,
                outputs = studySimplified.outputs
            )
        }
    }

    fun checkColor() {
        selectedStudy.value?.let { study ->
            chartIQ.getStudyParameters(study, StudyParameterType.Outputs) { list ->
                listOfConditions.value = listOfConditions.value?.map { item ->
                    if (item.condition.markerOption.color != null) {
                        item
                    } else {
                        val name =
                            item.condition.leftIndicator.substringBefore(ZERO_WIDTH_NON_JOINER)
                                .trim()
                        val color =
                            (list.firstOrNull { (it as? StudyParameter.Color)?.name == name } as? StudyParameter.Color)?.value
                                ?: (list.firstOrNull() as? StudyParameter.Color)?.value
                        item.copy(
                            displayedColor = color
                                ?: DEF_COLOR_STRING
                        )
                    }

                }
            }
        }
    }

    companion object {
        private const val DEF_COLOR_STRING = "#FF0000"
        private const val DEF_COLOR = 0xFF0000
        private const val ZERO_WIDTH_NON_JOINER = '\u200C'
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
