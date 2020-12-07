package com.chartiq.demo.ui.chart.panel.settings.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.databinding.FragmentChooseConfigBinding
import com.chartiq.demo.util.hideKeyboard

class AddConfigFragment : Fragment() {

    private lateinit var binding: FragmentChooseConfigBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseConfigBinding.inflate(inflater, container, false)

        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            addButton.setOnClickListener {
                // TODO: 10.11.20 Apply drawing tool parameters configuration change
                findNavController().navigateUp()
                requireContext().hideKeyboard(view?.windowToken)
            }
        }
    }
}
