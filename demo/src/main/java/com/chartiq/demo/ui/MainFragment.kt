package com.chartiq.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chartiq.demo.MainViewPagerAdapter
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val onNavItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val page = when (item.itemId) {
                R.id.navigation_chart -> MainViewPagerAdapter.MainNavigation.FRAGMENT_CHART.value
                R.id.navigation_study -> MainViewPagerAdapter.MainNavigation.FRAGMENT_STUDIES.value
                R.id.navigation_settings -> MainViewPagerAdapter.MainNavigation.FRAGMENT_SETTINGS.value
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
            }
            navView.apply {
                selectedItemId = R.id.navigation_study
                setOnNavigationItemSelectedListener(onNavItemSelectedListener)
            }
        }
    }
}
