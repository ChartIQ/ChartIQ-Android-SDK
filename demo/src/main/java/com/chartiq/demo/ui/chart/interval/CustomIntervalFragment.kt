package com.chartiq.demo.ui.chart.interval

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.databinding.FragmentChooseCustomIntervalBinding
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.interval.model.TimeUnit

class CustomIntervalFragment : Fragment() {

    private var binding: FragmentChooseCustomIntervalBinding? = null

    private val intervalTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding?.run {
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
        setupUI()
        return binding?.root
    }

    private fun setupUI() {
        binding?.run {
            selectValueAutoCompleteTextView.apply {
                val items = (MINIMAL_INTERVAL_VALUE..MAX_INTERVAL_VALUE)
                    .toList()
                    .map { it.toString() }
                setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, items))
                addTextChangedListener(intervalTextWatcher)
            }

            selectMeasurementAutoCompleteTextView.apply {
                val items = DEFAULT_MEASUREMENT_ITEMS
                    .map { it.toString().toLowerCase().capitalize() }
                setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, items))
                addTextChangedListener(intervalTextWatcher)
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            doneButton.setOnClickListener {
                ApplicationPrefs.Default(requireContext()).saveChartInterval(
                    Interval(
                        selectValueAutoCompleteTextView.text.toString().toInt(),
                        TimeUnit.valueOf(
                            selectMeasurementAutoCompleteTextView.text.toString().toUpperCase()
                        )
                    )
                )
                findNavController().navigateUp()
            }
        }
    }

    companion object {
        private const val MINIMAL_INTERVAL_VALUE = 1
        private const val MAX_INTERVAL_VALUE = 99

        private val DEFAULT_MEASUREMENT_ITEMS = listOf(
            TimeUnit.MINUTE,
            TimeUnit.HOUR,
            TimeUnit.DAY,
            TimeUnit.MONTH
        )
    }
}
