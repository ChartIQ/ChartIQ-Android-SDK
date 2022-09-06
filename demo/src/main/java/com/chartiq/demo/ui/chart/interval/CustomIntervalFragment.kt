package com.chartiq.demo.ui.chart.interval

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.chartiq.demo.databinding.FragmentChooseCustomIntervalBinding
import com.chartiq.demo.ui.chart.interval.list.IntervalItem
import com.chartiq.demo.ui.common.FullscreenDialogFragment
import com.chartiq.sdk.model.TimeUnit
import java.util.*

class CustomIntervalFragment : FullscreenDialogFragment() {

    private lateinit var binding: FragmentChooseCustomIntervalBinding

    private val viewModel: ChooseIntervalViewModel by activityViewModels()
    private val intervalTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.run {
                doneButton.isEnabled = selectValueAutoCompleteTextView.text.isNotEmpty()
                        && selectMeasurementAutoCompleteTextView.text.isNotEmpty()
            }
        }

        override fun afterTextChanged(s: Editable?) = Unit
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseCustomIntervalBinding.inflate(inflater, container, false)

        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            selectValueAutoCompleteTextView.apply {
                val items = (MINIMAL_INTERVAL_VALUE..MAX_INTERVAL_VALUE)
                    .toList()
                    .map { it.toString() }
                setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, items))
                addTextChangedListener(intervalTextWatcher)
            }

            selectMeasurementAutoCompleteTextView.apply {
                val items = DEFAULT_MEASUREMENT_LIST
                    .map { timeUnit ->
                        timeUnit
                            .toString()
                            .lowercase(Locale.getDefault())
                            .capitalize()
                    }
                setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, items))
                addTextChangedListener(intervalTextWatcher)
            }

            toolbar.setNavigationOnClickListener {
                dismiss()
            }
            doneButton.setOnClickListener {
                val interval = selectValueAutoCompleteTextView.text.toString().toInt()
                val unit = TimeUnit.valueOf(
                    selectMeasurementAutoCompleteTextView.text
                        .toString()
                        .uppercase(Locale.getDefault())
                )
                val item = IntervalItem(1, interval, unit, true)
                viewModel.onIntervalSelect(item)
                dismiss()
            }
        }
    }

    companion object {
        fun getInstance() = CustomIntervalFragment()

        private const val MINIMAL_INTERVAL_VALUE = 1
        private const val MAX_INTERVAL_VALUE = 99

        private val DEFAULT_MEASUREMENT_LIST = listOf(
            TimeUnit.MINUTE,
            TimeUnit.HOUR,
            TimeUnit.DAY,
            TimeUnit.MONTH
        )
    }
}
