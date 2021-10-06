package com.chartiq.demo.ui.study.studydetails

import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.*
import com.chartiq.demo.ui.common.colorpicker.convertStringColorToInt
import com.chartiq.sdk.model.study.StudyParameter
import java.util.*

class StudyDetailsAdapter : RecyclerView.Adapter<StudyDetailsAdapter.ParameterViewHolder<StudyParameter>>() {

    var listener: StudyParameterListener? = null

    var items = listOf<StudyParameter>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParameterViewHolder<StudyParameter> {
        val inflater = LayoutInflater.from(parent.context)

        @Suppress("UNCHECKED_CAST")
        return when (viewType) {
            ParamViewType.TEXT.ordinal -> TextViewHolder(
                ItemStudyDetailTextBinding.inflate(inflater, parent, false)
            )
            ParamViewType.NUMBER.ordinal -> NumberViewHolder(
                ItemStudyDetailNumberBinding.inflate(inflater, parent, false)
            )
            ParamViewType.COLOR.ordinal -> ColorViewHolder(
                ItemStudyDetailColorBinding.inflate(inflater, parent, false)
            )
            ParamViewType.TEXT_COLOR.ordinal -> TextColorViewHolder(
                ItemStudyDetailColorTextBinding.inflate(inflater, parent, false)
            )
            ParamViewType.CHECKBOX.ordinal -> CheckboxViewHolder(
                ItemStudyParamCheckboxBinding.inflate(inflater, parent, false)
            )
            ParamViewType.SELECT.ordinal -> SelectViewHolder(
                ItemSelectStudyParamBinding.inflate(inflater, parent, false)
            )
            else -> {
                throw IllegalStateException("Invalid viewType $viewType")
            }
        }
                as ParameterViewHolder<StudyParameter>
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

    override fun onBindViewHolder(holder: ParameterViewHolder<StudyParameter>, position: Int) {
        when (val item = items[position]) {
            is StudyParameter.Text -> holder.bind(item)
            is StudyParameter.Number -> holder.bind(item)
            is StudyParameter.Checkbox -> holder.bind(item)
            is StudyParameter.Color -> holder.bind(item)
            is StudyParameter.TextColor -> holder.bind(item)
            is StudyParameter.Select -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    abstract class ParameterViewHolder<T : StudyParameter>(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(studyParameter: T)
    }

    inner class TextViewHolder(private val binding: ItemStudyDetailTextBinding) :
        ParameterViewHolder<StudyParameter.Text>(binding.root) {

        private var customTextWatcher: CustomTextWatcher? = null

        override fun bind(studyParameter: StudyParameter.Text) {
            with(binding) {
                parameterInputLayout.hint = (studyParameter as StudyParameter.Text).heading
                parameterNumberEditText.apply {
                    customTextWatcher?.let { removeTextChangedListener(it) }
                    setText(studyParameter.value)
                    setSelection(studyParameter.value.length)
                    customTextWatcher = CustomTextWatcher {
                        listener?.onTextParamChange(studyParameter, it.toString())
                    }
                    addTextChangedListener(customTextWatcher)
                }
            }
        }
    }

    inner class NumberViewHolder(private val binding: ItemStudyDetailNumberBinding) :
        ParameterViewHolder<StudyParameter.Number>(binding.root) {

        private var customTextWatcher: CustomTextWatcher? = null

        override fun bind(studyParameter: StudyParameter.Number) {
            with(binding) {
                parameterNumberInputLayout.hint = studyParameter.heading
                parameterNumberEditText.apply {
                    customTextWatcher?.let {
                        removeTextChangedListener(it)
                    }
                    if (studyParameter.name.toLowerCase(Locale.ENGLISH).contains(NUMBER_TYPE_SIGN)) {
                        inputType = InputType.TYPE_CLASS_NUMBER
                        setText(studyParameter.value.toInt().toString())
                        setSelection(studyParameter.value.toInt().toString().length)

                    } else {
                        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                        setText(studyParameter.value.toString())
                        setSelection(studyParameter.value.toString().length)
                    }
                    customTextWatcher = CustomTextWatcher {
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
                    addTextChangedListener(customTextWatcher)
                }
            }
        }
    }

    inner class CheckboxViewHolder(private val binding: ItemStudyParamCheckboxBinding) :
        ParameterViewHolder<StudyParameter.Checkbox>(binding.root) {
        override fun bind(studyParameter: StudyParameter.Checkbox) {
            with(binding) {
                parameterTextView.text = studyParameter.name
                parameterSwitch.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = studyParameter.value
                    setOnCheckedChangeListener { _, isChecked ->
                        listener?.onCheckboxParamChange(studyParameter, isChecked)
                    }
                }
            }
        }
    }

    inner class ColorViewHolder(private val binding: ItemStudyDetailColorBinding) :
        ParameterViewHolder<StudyParameter.Color>(binding.root) {
        override fun bind(studyParameter: StudyParameter.Color) {
            val colorValue = studyParameter.value
            with(binding) {
                parameterTextView.text = studyParameter.name
                (colorImageView.background as GradientDrawable).setColor(
                    convertStringColorToInt(
                        colorValue,
                        root.resources
                    )
                )
                colorImageView.setOnClickListener { listener?.onColorParamChange(studyParameter) }
            }
        }
    }

    inner class TextColorViewHolder(private val binding: ItemStudyDetailColorTextBinding) :
        ParameterViewHolder<StudyParameter.TextColor>(binding.root) {

        private var customTextWatcher: CustomTextWatcher? = null

        override fun bind(studyParameter: StudyParameter.TextColor) {
            val colorValue = studyParameter.color
                ?: studyParameter.defaultColor
            val numValue = studyParameter.value ?: studyParameter.defaultValue
            with(binding) {
                parameterTextColorTextView.text = studyParameter.name
                (colorImageView.background as GradientDrawable).setColor(
                    convertStringColorToInt(
                        colorValue,
                        root.resources
                    )
                )
                colorImageView.setOnClickListener {
                    listener?.onColorParamChange(studyParameter)
                }
                customTextWatcher?.let { parameterTextColorEditText.removeTextChangedListener(it) }
                parameterTextColorEditText.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
                    if (parameterTextColorEditText.text.isNullOrEmpty()) {
                        val defaultValue = "0.0"
                        parameterTextColorEditText.setText(defaultValue)
                        listener?.onTextParamChange(studyParameter, defaultValue)
                    }
                }

                parameterTextColorEditText.setText(numValue.toString())

                customTextWatcher = CustomTextWatcher {
                    if (!it.isNullOrEmpty()) {
                        parameterTextColorEditText.error = null
                        listener?.onTextParamChange(studyParameter, it.toString())
                    }
                }
                parameterTextColorEditText.addTextChangedListener(customTextWatcher)
            }
        }
    }

    inner class SelectViewHolder(private val binding: ItemSelectStudyParamBinding) :
        ParameterViewHolder<StudyParameter.Select>(binding.root) {
        override fun bind(studyParameter: StudyParameter.Select) {
            with(binding) {
                parameterSelectTextView.text = (studyParameter as StudyParameter.Select).heading
                parameterSelectValueTextView.text = studyParameter.options[studyParameter.value]
                root.setOnClickListener { listener?.onSelectParamChange(studyParameter) }
            }
        }
    }

    companion object {
        private const val NUMBER_TYPE_SIGN = "period"
    }

    interface StudyParameterListener {
        fun onCheckboxParamChange(parameter: StudyParameter.Checkbox, isChecked: Boolean)

        fun onTextParamChange(parameter: StudyParameter, newValue: String)

        fun onNumberParamChange(parameter: StudyParameter.Number, newValue: Double?)

        fun onColorParamChange(studyParameter: StudyParameter)

        fun onSelectParamChange(studyParameter: StudyParameter.Select)
    }

    class CustomTextWatcher(val block: (s: Editable?) -> Unit) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            block(s)
        }

    }
}

