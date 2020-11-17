package com.chartiq.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.MainViewPagerAdapter
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentMainBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.sdk.ChartIQ
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private val chartIQHandler: ChartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }

    private val mainViewModel: MainViewModel by activityViewModels(factoryProducer = {
        MainViewModel.ViewModelFactory(
            ChartIQNetworkManager(),
            ApplicationPrefs.Default(requireContext()),
            chartIQHandler
        )
    })

    private lateinit var binding: FragmentMainBinding

    private val onNavItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val page = when (item.itemId) {
                R.id.navigation_chart -> MainViewPagerAdapter.FRAGMENT_CHART
                R.id.navigation_study -> MainViewPagerAdapter.FRAGMENT_STUDIES
                R.id.navigation_settings -> MainViewPagerAdapter.FRAGMENT_SETTINGS
                else -> throw IllegalStateException()
            }
            binding.mainViewPager.setCurrentItem(page, true)
            true
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            mainViewPager.apply {
                adapter = MainViewPagerAdapter(this@MainFragment)
                isUserInputEnabled = false
                currentItem = MainViewPagerAdapter.FRAGMENT_CHART
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        if (position == MainViewPagerAdapter.FRAGMENT_STUDIES) {
                            reloadData()
                        }
                    }
                })
            }
            navView.apply {
                selectedItemId = R.id.navigation_chart
                setOnNavigationItemSelectedListener(onNavItemSelectedListener)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }

    private fun reloadData() {
        mainViewModel.fetchActiveStudyData()
    }
}
