package com.chartiq.demo.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private val chartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }
    private val settingsViewModel: SettingsViewModel by viewModels(factoryProducer = {
        SettingsViewModel.ViewModelFactory(chartIQ)
    })
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {

        }
    }
}
