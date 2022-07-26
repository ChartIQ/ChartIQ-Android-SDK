package com.chartiq.demo.ui.signal.addsignal.add_condition

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.R
import com.chartiq.demo.localization.LocalizationManager
import com.chartiq.demo.ui.signal.addsignal.ConditionItem
import com.chartiq.sdk.model.signal.Condition
import com.chartiq.sdk.model.signal.MarkerOption
import com.chartiq.sdk.model.signal.SignalMarkerType
import com.chartiq.sdk.model.signal.SignalOperator
import com.chartiq.sdk.model.signal.SignalPosition
import com.chartiq.sdk.model.signal.SignalShape
import com.chartiq.sdk.model.signal.SignalSize
import com.chartiq.sdk.model.study.Study
import java.util.*

class AddConditionViewModel(
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
    val selectedRightValue = MutableLiveData(0.0)
    val label = MutableLiveData("X")
    val isShowRightIndicator = MutableLiveData(false)
    val isShowRightValue = MutableLiveData(false)
    val isShowAppearanceSettings = MediatorLiveData<Boolean>().apply {
        value = false
    }
    val isShowSaveSettings = MediatorLiveData<Boolean>().apply {
        value = true
    }
    val isSaveAvailable = MediatorLiveData<Boolean>().apply {
        value = false
    }

    val currentColor = MutableLiveData(0xFF0000)
    val conditionItem = MutableLiveData<ConditionItem>()
    private val conditionUUID = MutableLiveData(UUID.randomUUID())

    init {
        isShowAppearanceSettings.addSource(selectedOperator) {
            isShowAppearanceSettings.value = true
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
                                || (selectedRightIndicator.value == "Value" && selectedRightValue.value != null)
                        )
            }
    }

    fun onSelectLeftIndicator(text: String) {
        selectedLeftIndicator.value = text
    }

    fun onSelectRightIndicator(text: String) {
        selectedRightIndicator.value = text
    }

    fun onChooseColor(color: Int) {
        currentColor.value = color
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
        if (selectedOperator.value != null) {
            isShowRightValue.value = true
        }
    }

    fun onSaveCondition(label: String) {
        val rightIndicator = if (isShowRightIndicator.value == true) {
            if (isShowRightValue.value == true) {
                selectedRightValue.value
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
                rightIndicator = rightIndicator.toString(),
                signalOperator = selectedOperator.value!!,
                markerOption = MarkerOption(
                    type = selectedMarker.value!!,
                    color = String.format("#%06X", 0xFFFFFF and currentColor.value!!),
                    signalShape = selectedSignalShape.value!!,
                    signalSize = selectedSignalSize.value!!,
                    label = label,
                    signalPosition = selectedSignalPosition.value!!,
                )
            ),
            UUID = conditionUUID.value!!
        )
    }

    fun onRightValueSelected(value: String) {
        selectedRightValue.value = value.toDoubleOrNull() ?: 0.0
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
            label.value = item.condition.markerOption.label
            currentColor.value = Color.parseColor(item.condition.markerOption.color)
            selectedLeftIndicator.value = item.condition.leftIndicator
            if (item.condition.rightIndicator != null) {
                try {
                    selectedRightValue.value =
                        item.condition.rightIndicator?.toDouble()
                    selectedRightIndicator.value = "Value"
                    isShowRightValue.value = true
                } catch (e: Exception) {
                    selectedRightIndicator.value = item.condition.rightIndicator
                }
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
        color?.let { currentColor.value = it }
    }


    class ViewModelFactory(
        private val localizationManager: LocalizationManager,
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(
                    LocalizationManager::class.java,
                    Context::class.java
                )
                .newInstance(localizationManager, context)
        }
    }
}