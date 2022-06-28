package com.chartiq.demo.ui.signal.addsignal.add_condition

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.sdk.model.signal.Condition
import com.chartiq.sdk.model.signal.MarkerOption
import com.chartiq.sdk.model.signal.SignalMarkerType
import com.chartiq.sdk.model.signal.SignalOperator
import com.chartiq.sdk.model.signal.SignalPosition
import com.chartiq.sdk.model.signal.SignalShape
import com.chartiq.sdk.model.signal.SignalSize

class AddConditionViewModel : ViewModel() {

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
    private val selectedMarker = MutableLiveData(SignalMarkerType.PAINTBAR)
    private val selectedSignalShape = MutableLiveData(SignalShape.CIRCLE)
    private val selectedValue = MutableLiveData<SignalOperator>()
    private val selectedSignalSize = MutableLiveData(SignalSize.M)
    private val selectedSignalPosition = MutableLiveData(SignalPosition.ABOVE_CANDLE)
    private val selectedIndicator1 = MutableLiveData("")
    private val selectedIndicator2 = MutableLiveData("")
    private val selectedValue2 = MutableLiveData("")
    val isShowIndicator2 = MutableLiveData(false)
    val isShowIndicator2Value = MutableLiveData(false)
    val isShowSaveSettings = MutableLiveData(true)
    val isSaveAvailable = MediatorLiveData<Boolean>().apply {
        value = false
    }

    val currentColor = MutableLiveData(0xFFFFFF)
    val condition = MutableLiveData<Condition>()

    init {
        isSaveAvailable.addSource(selectedValue) {
            checkSaveAvailability()
        }
        isSaveAvailable.addSource(selectedIndicator2) {
            checkSaveAvailability()
        }
        isSaveAvailable.addSource(selectedValue2) {
            checkSaveAvailability()
        }
    }

    private fun checkSaveAvailability() {
        isSaveAvailable.value =
            if (selectedValue.value == SignalOperator.INCREASES
                || selectedValue.value == SignalOperator.DECREASES
                || selectedValue.value == SignalOperator.DOES_NOT_CHANGE
                || selectedValue.value == SignalOperator.TURNS_UP
                || selectedValue.value == SignalOperator.TURNS_DOWN
            ) {
                true
            } else {
                (selectedIndicator2.value != "" && selectedIndicator2.value != "Value") || (selectedIndicator2.value == "Value" && selectedValue2.value != "")
            }
    }

    fun onSelectIndicator1(text: String) {
        selectedIndicator1.value = text
    }

    fun onSelectIndicator2(text: String) {
        selectedIndicator2.value = text
    }

    fun onChooseColor(color: Int) {
        currentColor.value = color
    }

    fun onValueSelected(index: Int) {
        if (index > -1) {
            selectedValue.value = listOfValues.value!![index]
            isShowIndicator2.value =
                !(selectedValue.value!! == SignalOperator.INCREASES
                        || selectedValue.value!! == SignalOperator.DECREASES
                        || selectedValue.value!! == SignalOperator.DOES_NOT_CHANGE
                        || selectedValue.value!! == SignalOperator.TURNS_UP
                        || selectedValue.value!! == SignalOperator.TURNS_DOWN)
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
        isShowIndicator2Value.value = false
    }

    fun onIndicator2ValueSelected() {
        isShowIndicator2Value.value = true
    }

    fun onSaveCondition(label: String) {
        if (isShowIndicator2Value.value == true) {
            condition.value = Condition(
                lhs = selectedIndicator1.value ?: "",
                rhs = selectedValue2.value!!,
                signalOperator = selectedValue.value!!,
                color = String.format("#%06X", 0xFFFFFF and currentColor.value!!),
                markerOption = MarkerOption(
                    signalShape = selectedSignalShape.value!!,
                    signalSize = selectedSignalSize.value!!,
                    label = label,
                    signalPosition = selectedSignalPosition.value!!,
                )
            )
        } else {
            condition.value = Condition(
                lhs = selectedIndicator1.value ?: "",
                rhs = selectedIndicator2.value ?: "",
                signalOperator = selectedValue.value!!,
                color = String.format("#%06X", 0xFFFFFF and currentColor.value!!),
                markerOption = MarkerOption(
                    signalShape = selectedSignalShape.value!!,
                    signalSize = selectedSignalSize.value!!,
                    label = label,
                    signalPosition = selectedSignalPosition.value!!,
                )
            )
        }
    }

    fun onValue2Selected(value2: String) {
        selectedValue2.value = value2
    }

    fun onMarkerTypeSelected(index: Int) {
        selectedMarker.value = listOfMarkerTypes.value!![index]
        isShowSaveSettings.value = when (selectedMarker.value) {
            SignalMarkerType.MARKER -> true
            SignalMarkerType.PAINTBAR -> false
            else -> true
        }
    }

    class ViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor()
                .newInstance()
        }
    }
}