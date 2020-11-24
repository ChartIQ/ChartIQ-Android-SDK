package com.chartiq.demo.ui.study.studydetails

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.*
import com.chartiq.sdk.model.StudyParameter

class StudyDetailsAdapter : RecyclerView.Adapter<StudyDetailsAdapter.ParameterViewHolder>() {

    var listener: StudyParameterListener? = null

    var items = listOf<StudyParameter>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParameterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ParamViewType.TEXT.ordinal -> TextViewHolder(
                ItemTextStudyParamBinding.inflate(inflater, parent, false)
            )
            ParamViewType.NUMBER.ordinal -> NumberViewHolder(
                ItemNumberStudyParamBinding.inflate(inflater, parent, false)
            )
            ParamViewType.COLOR.ordinal -> ColorViewHolder(
                ItemColorStudyParamBinding.inflate(inflater, parent, false)
            )
            ParamViewType.TEXT_COLOR.ordinal -> TextColorViewHolder(
                ItemTextColorStudyParamBinding.inflate(inflater, parent, false)
            )
            ParamViewType.CHECKBOX.ordinal -> CheckboxViewHolder(
                ItemCheckboxStudyParamBinding.inflate(inflater, parent, false)
            )
            ParamViewType.SELECT.ordinal -> SelectViewHolder(
                ItemSelectStudyParamBinding.inflate(inflater, parent, false)
            )
            else -> {
                throw IllegalStateException("Invalid viewType $viewType")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is StudyParameter.Text -> ParamViewType.TEXT.ordinal
            is StudyParameter.Number -> ParamViewType.NUMBER.ordinal
            is StudyParameter.Checkbox -> ParamViewType.CHECKBOX.ordinal
            is StudyParameter.Color -> ParamViewType.COLOR.ordinal
            is StudyParameter.TextColor -> ParamViewType.TEXT_COLOR.ordinal
            is StudyParameter.Select -> ParamViewType.SELECT.ordinal
        }
    }

    override fun onBindViewHolder(holder: ParameterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    abstract class ParameterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(studyParameter: StudyParameter)
    }

    inner class TextViewHolder(private val binding: ItemTextStudyParamBinding) : ParameterViewHolder(binding.root) {
        override fun bind(studyParameter: StudyParameter) {
            with(binding) {
                parameterInputLayout.hint = (studyParameter as StudyParameter.Text).heading
                parameterNumberEditText.apply {
                    setText(studyParameter.value)
                    setSelection(studyParameter.value.length)
                    addTextChangedListener {
                        listener?.onTextParamChange(studyParameter, it.toString())
                    }
                }
            }
        }
    }

    inner class NumberViewHolder(private val binding: ItemNumberStudyParamBinding) : ParameterViewHolder(binding.root) {
        override fun bind(studyParameter: StudyParameter) {
            with(binding) {
                parameterNumberInputLayout.hint = (studyParameter as StudyParameter.Number).heading
                parameterNumberEditText.apply {
                    setText(studyParameter.value.toString())
                    setSelection(studyParameter.value.toString().length)
                    addTextChangedListener {
                        if (it.isNullOrEmpty()) {
                            parameterNumberInputLayout.error =
                                context.getString(R.string.study_details_validation_error_empty_value)
                            listener?.onNumberParamChange(
                                studyParameter,
                                null
                            )
                        } else {
                            parameterNumberInputLayout.error = null
                            listener?.onNumberParamChange(
                                studyParameter,
                                it.toString().toDouble()
                            )
                        }
                    }
                }
            }
        }
    }

    inner class CheckboxViewHolder(private val binding: ItemCheckboxStudyParamBinding) :
        ParameterViewHolder(binding.root) {
        override fun bind(studyParameter: StudyParameter) {
            with(binding) {
                parameterTextView.text = studyParameter.name
                parameterSwitch.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = (studyParameter as StudyParameter.Checkbox).value
                    setOnCheckedChangeListener { _, isChecked ->
                        listener?.onCheckboxParamChange(studyParameter, isChecked)
                    }
                }
            }
        }
    }

    inner class ColorViewHolder(private val binding: ItemColorStudyParamBinding) : ParameterViewHolder(binding.root) {
        override fun bind(studyParameter: StudyParameter) {
            val colorValue = (studyParameter as StudyParameter.Color).value
            with(binding) {
                parameterTextView.text = studyParameter.name
                parameterColor.backgroundTintList = ColorStateList.valueOf(
                    if (colorValue == StudyParameter.AUTO_VALUE) {
                        Color.BLACK
                    } else {
                        Color.parseColor(colorValue)
                    }
                )
                parameterColorLayout.setOnClickListener { listener?.onColorParamChange(studyParameter) }
            }
        }
    }

    inner class TextColorViewHolder(private val binding: ItemTextColorStudyParamBinding) :
        ParameterViewHolder(binding.root) {
        override fun bind(studyParameter: StudyParameter) {
            val colorValue = (studyParameter as StudyParameter.TextColor).color ?: studyParameter.defaultColor
            val numValue = studyParameter.value ?: studyParameter.defaultValue
            with(binding) {
                parameterTextColorTextView.text = studyParameter.name
                parameterTextColor.backgroundTintList = ColorStateList.valueOf(
                    if (colorValue == StudyParameter.AUTO_VALUE) {
                        Color.BLACK
                    } else {
                        Color.parseColor(colorValue)
                    }
                )
                parameterTextColorLayout.setOnClickListener { listener?.onColorParamChange(studyParameter) }
                parameterTextColorEditText.setText(numValue.toString())
                parameterTextColorEditText.addTextChangedListener {
                    if (it.isNullOrEmpty()) {
                        parameterTextColorEditText.error =
                            root.context.getString(R.string.study_details_validation_error_empty_value)
                        listener?.onTextParamChange(studyParameter, it.toString())
                    } else {
                        parameterTextColorEditText.error = null
                        listener?.onTextParamChange(studyParameter, it.toString())
                    }
                }
            }
        }
    }

    inner class SelectViewHolder(private val binding: ItemSelectStudyParamBinding) : ParameterViewHolder(binding.root) {
        override fun bind(studyParameter: StudyParameter) {
            with(binding) {
                parameterSelectTextView.text = (studyParameter as StudyParameter.Select).heading
                parameterSelectValueTextView.text = studyParameter.options[studyParameter.value]
                root.setOnClickListener { listener?.onSelectParamChange(studyParameter) }
            }
        }
    }

    interface StudyParameterListener {
        fun onCheckboxParamChange(parameter: StudyParameter.Checkbox, isChecked: Boolean)
        fun onTextParamChange(parameter: StudyParameter, newValue: String)
        fun onNumberParamChange(parameter: StudyParameter.Number, newValue: Double?)
        fun onColorParamChange(studyParameter: StudyParameter)
        fun onSelectParamChange(studyParameter: StudyParameter.Select)
    }
}

