package com.chartiq.demo.ui.signal.addsignal.add_condition

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.R
import com.chartiq.demo.localization.LocalizationManager
import com.chartiq.demo.ui.settings.chartstyle.ChartTypeItem
import com.chartiq.demo.ui.signal.addsignal.ConditionItem
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.charttype.ChartAggregationType
import com.chartiq.sdk.model.signal.Condition
import com.chartiq.sdk.model.signal.MarkerOption
import com.chartiq.sdk.model.signal.SignalMarkerType
import com.chartiq.sdk.model.signal.SignalOperator
import com.chartiq.sdk.model.signal.SignalPosition
import com.chartiq.sdk.model.signal.SignalShape
import com.chartiq.sdk.model.signal.SignalSize
import com.chartiq.sdk.model.study.Study
import com.chartiq.sdk.model.study.StudyParameter
import com.chartiq.sdk.model.study.StudyParameterType
import java.math.BigDecimal
import java.util.*

class AddConditionViewModel(
    private val chartIQ: ChartIQ,
    private val localizationManager: LocalizationManager,
    private val context: Context
) : ViewModel() {

    val listOfValues = MutableLiveData<List<SignalOperator>>().apply {
        value = listOf(
            SignalOperator.GREATER_THAN,
            SignalOperator.LESS_THAN,
            SignalOperator.EQUAL_TO,
            SignalOperator.CROSSES,
            SignalOperator.CROSSES_ABOVE,
            SignalOperator.CROSSES_BELOW,
            SignalOperator.TURNS_UP,
            SignalOperator.TURNS_DOWN,
            SignalOperator.INCREASES,
            SignalOperator.DECREASES,
            SignalOperator.DOES_NOT_CHANGE
        )
    }

    val listOfSizes = MutableLiveData<List<SignalSize>>().apply {
        value = listOf(
            SignalSize.S,
            SignalSize.M,
            SignalSize.L
        )
    }

    val listOfPositions = MutableLiveData<List<SignalPosition>>().apply {
        value = listOf(
            SignalPosition.ABOVE_CANDLE,
            SignalPosition.BELOW_CANDLE,
            SignalPosition.ON_CANDLE
        )
    }

    val listOfShapes = MutableLiveData<List<SignalShape>>().apply {
        value = listOf(
            SignalShape.CIRCLE,
            SignalShape.DIAMOND,
            SignalShape.SQUARE
        )
    }

    val listOfMarkerTypes = MutableLiveData<List<SignalMarkerType>>().apply {
        value = listOf(
            SignalMarkerType.MARKER,
            SignalMarkerType.PAINTBAR
        )
    }
    private val selectedStudy = MutableLiveData<Study>()
    val selectedMarker = MutableLiveData(SignalMarkerType.MARKER)
    val selectedSignalShape = MutableLiveData(SignalShape.CIRCLE)
    val selectedOperator = MutableLiveData<SignalOperator?>()
    val selectedSignalSize = MutableLiveData(SignalSize.M)
    val selectedSignalPosition = MutableLiveData(SignalPosition.ABOVE_CANDLE)
    val selectedLeftIndicator = MutableLiveData("")
    val selectedRightIndicator = MutableLiveData<String?>()
    val selectedRightValue = MutableLiveData("0.0")
    val label = MutableLiveData("X")
    val isShowRightIndicator = MutableLiveData(false)
    val isShowRightValue = MutableLiveData(false)
    val isShowAppearanceSettings = MediatorLiveData<Boolean>().apply {
        value = false
    }
    private val shouldShowSettings = MutableLiveData<Boolean>()
    val isShowSaveSettings = MediatorLiveData<Boolean>().apply {
        value = true
    }
    val isSaveAvailable = MediatorLiveData<Boolean>().apply {
        value = false
    }
    val isAttentionVisible = MediatorLiveData<Boolean>()

    val currentColor = MediatorLiveData<Int>().apply {
        value = DEF_COLOR
    } // color for UI
    private val selectedColor = MutableLiveData<Int>() // real selected color for js
    val conditionItem = MutableLiveData<ConditionItem>()
    private val conditionUUID = MutableLiveData(UUID.randomUUID())
    val chartStyle = MutableLiveData<ChartTypeItem>()

    init {
        isShowAppearanceSettings.addSource(selectedOperator) {
            isShowAppearanceSettings.value = true && shouldShowSettings.value ?: true
        }
        isSaveAvailable.addSource(selectedOperator) {
            checkSaveAvailability()
        }
        isSaveAvailable.addSource(selectedRightIndicator) {
            checkSaveAvailability()
        }
        isSaveAvailable.addSource(selectedRightValue) {
            checkSaveAvailability()
        }
        isShowSaveSettings.addSource(selectedMarker) { marker ->
            isShowSaveSettings.value = when (marker) {
                SignalMarkerType.MARKER -> true
                SignalMarkerType.PAINTBAR -> false
                else -> true
            }
        }
        currentColor.addSource(selectedColor) { color ->
            currentColor.value = color
        }
        isAttentionVisible.addSource(selectedMarker) { markerType ->
            chartStyle.value?.name?.let { name ->
                isAttentionVisible.value =
                    markerType == SignalMarkerType.PAINTBAR && (name == ChartAggregationType.KAGI.name || name == ChartAggregationType.PANDF.name)
            }
        }
    }

    fun setStudy(study: Study) {
        selectedStudy.value = study
    }

    private fun checkSaveAvailability() {
        isSaveAvailable.value =
            if (selectedOperator.value == SignalOperator.INCREASES
                || selectedOperator.value == SignalOperator.DECREASES
                || selectedOperator.value == SignalOperator.DOES_NOT_CHANGE
                || selectedOperator.value == SignalOperator.TURNS_UP
                || selectedOperator.value == SignalOperator.TURNS_DOWN
            ) {
                true
            } else {
                selectedOperator.value != null
                        && (
                        (selectedRightIndicator.value != null && selectedRightIndicator.value != "Value")
                                || (selectedRightIndicator.value == "Value" && selectedRightValue.value?.isNotEmpty() == true)
                        )
            }
    }

    fun onSelectLeftIndicator(text: String) {
        selectedLeftIndicator.value = text
        if (selectedColor.value == null) {
            selectedStudy.value?.let { study ->
                chartIQ.getStudyParameters(study, StudyParameterType.Outputs) { list ->
                    val name = text.substringBefore(ZERO_WIDTH_NON_JOINER).trim()
                    val color =
                        (list.firstOrNull { (it as? StudyParameter.Color)?.name == name } as? StudyParameter.Color)?.value
                    if (color != null && selectedColor.value == null) {
                        currentColor.value =
                            Color.parseColor(color)
                    }
                }
            }
        }
    }

    fun onSelectRightIndicator(text: String) {
        if (text != selectedRightIndicator.value) {
            selectedRightIndicator.value = text
        }
    }

    fun onChooseColor(color: Int) {
        this.selectedColor.value = color
    }

    fun onValueSelected(index: Int) {
        if (index > -1) {
            selectedOperator.value = listOfValues.value!![index]
            isShowRightIndicator.value =
                !(selectedOperator.value!! == SignalOperator.INCREASES
                        || selectedOperator.value!! == SignalOperator.DECREASES
                        || selectedOperator.value!! == SignalOperator.DOES_NOT_CHANGE
                        || selectedOperator.value!! == SignalOperator.TURNS_UP
                        || selectedOperator.value!! == SignalOperator.TURNS_DOWN)
            if (isShowRightIndicator.value == false) {
                selectedRightIndicator.value = null
            } else {
                if (localizationManager.getTranslationFromValue(
                        context.getString(R.string.value),
                        context
                    ) == selectedRightIndicator.value
                ) {
                    isShowRightValue.value = true
                }
            }
        }
    }

    fun onShapeSelected(index: Int) {
        selectedSignalShape.value = listOfShapes.value!![index]
    }

    fun onSizeSelected(index: Int) {
        selectedSignalSize.value = listOfSizes.value!![index]
    }

    fun onPositionSelected(index: Int) {
        selectedSignalPosition.value = listOfPositions.value!![index]
    }

    fun onIndicator2ValueUnSelected() {
        isShowRightValue.value = false
    }

    fun onIndicator2ValueSelected() {
        if (selectedOperator.value != null && selectedRightIndicator.value != null) {
            isShowRightValue.value = true
        }
    }

    fun onSaveCondition(label: String) {
        val rightIndicator = if (isShowRightIndicator.value == true) {
            if (isShowRightValue.value == true) {
                selectedRightValue.value.toString()
            } else {
                selectedRightIndicator.value
            }
        } else {
            null
        }
        conditionItem.value = ConditionItem(
            title = "",
            description = "",
            condition = Condition(
                leftIndicator = selectedLeftIndicator.value ?: "",
                rightIndicator = rightIndicator,
                signalOperator = selectedOperator.value!!,
                markerOption = MarkerOption(
                    type = selectedMarker.value!!,
                    color = if (selectedColor.value != null) {
                        String.format("#%06X", FORMAT_COLOR and selectedColor.value!!)
                    } else {
                        null
                    },
                    signalShape = selectedSignalShape.value!!,
                    signalSize = selectedSignalSize.value!!,
                    label = label,
                    signalPosition = selectedSignalPosition.value!!,
                )
            ),
            displayedColor = String.format("#%06X", FORMAT_COLOR and currentColor.value!!),
            UUID = conditionUUID.value!!
        )
    }

    fun onRightValueSelected(value: String) {
        selectedRightValue.value = value
    }

    fun onMarkerTypeSelected(index: Int) {
        selectedMarker.value = listOfMarkerTypes.value!![index]
    }

    fun setCondition(conditionItem: ConditionItem?) {
        conditionItem?.let { item ->
            conditionUUID.value = item.UUID
            selectedMarker.value = item.condition.markerOption.type
            selectedSignalShape.value = item.condition.markerOption.signalShape
            selectedOperator.value = item.condition.signalOperator
            selectedSignalSize.value = item.condition.markerOption.signalSize
            selectedSignalPosition.value = item.condition.markerOption.signalPosition
            label.value = if (item.condition.markerOption.type == SignalMarkerType.MARKER) {
                item.condition.markerOption.label
            } else {
                "X"
            }
            if (item.condition.markerOption.color == null) {
                currentColor.value = Color.parseColor(item.displayedColor)
            } else {
                selectedColor.value = Color.parseColor(item.condition.markerOption.color)
            }
            selectedLeftIndicator.value = item.condition.leftIndicator
            if (item.condition.rightIndicator != null) {
                try {
                    selectedRightValue.value =
                        BigDecimal(item.condition.rightIndicator).toString()
                    selectedRightIndicator.value = "Value"
                    isShowRightValue.value = true
                } catch (e: Exception) {
                    selectedRightIndicator.value = item.condition.rightIndicator
                    isShowRightValue.value = false
                }
            } else {
                selectedRightIndicator.value = null
                isShowRightValue.value = false
            }

            isShowRightIndicator.value =
                !(selectedOperator.value!! == SignalOperator.INCREASES
                        || selectedOperator.value!! == SignalOperator.DECREASES
                        || selectedOperator.value!! == SignalOperator.DOES_NOT_CHANGE
                        || selectedOperator.value!! == SignalOperator.TURNS_UP
                        || selectedOperator.value!! == SignalOperator.TURNS_DOWN)
            isSaveAvailable.value = true
        }
    }

    fun onLabelChanged(text: String) {
        if (label.value != text) {
            label.value = text
        }
    }

    fun setColor(color: Int?) {
        color?.let { this.currentColor.value = it }
    }

    fun setSettingsVisibility(shouldShowSettings: Boolean) {
        this.shouldShowSettings.value = shouldShowSettings
    }

    fun checkColor() {
        if (selectedColor.value == null) {
            selectedStudy.value?.let { study ->
                chartIQ.getStudyParameters(study, StudyParameterType.Outputs) { list ->
                    val color =
                        (list.firstOrNull {
                            (it as? StudyParameter.Color)?.name == (selectedLeftIndicator.value
                                ?: "")
                        } as? StudyParameter.Color)?.value
                    if (color != null) {
                        currentColor.value =
                            Color.parseColor(color)
                    }
                }
            }
        }
    }

    fun setChartStyle(chartStyle: ChartTypeItem?) {
        chartStyle?.let {
            this.chartStyle.value = it
        }
    }

    companion object {
        private const val DEF_COLOR = 0xFF0000
        private const val FORMAT_COLOR = 0xFFFFFF
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