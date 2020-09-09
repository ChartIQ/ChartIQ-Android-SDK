package com.chartiq.demo.ui.chart.interval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.R
import com.chartiq.demo.ui.chart.interval.model.TimeUnit

class CustomIntervalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_choose_custom_interval, container, false)
        setupUI(root)

        return root
    }

    private fun setupUI(root: View) {
        root.findViewById<AutoCompleteTextView>(R.id.selectValueAutoCompleteTextView).apply {
            val items = (MINIMAL_INTERVAL_VALUE..MAX_INTERVAL_VALUE).toList().map { it.toString() }
            setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, items))
        }
        root.findViewById<AutoCompleteTextView>(R.id.selectMeasurementAutoCompleteTextView).apply {
            val items = listOf(
                TimeUnit.MINUTE,
                TimeUnit.HOUR,
                TimeUnit.DAY,
                TimeUnit.MONTH
            ).map { it.toString().toLowerCase().capitalize() }
            setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, items))
        }

        root.findViewById<Toolbar>(R.id.toolbar).apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    companion object {
        private const val MINIMAL_INTERVAL_VALUE = 1
        private const val MAX_INTERVAL_VALUE = 99
    }
}
