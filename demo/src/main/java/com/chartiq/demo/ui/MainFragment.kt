package com.chartiq.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.chartiq.demo.MainViewPagerAdapter
import com.chartiq.demo.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment: Fragment(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewPager: ViewPager2
    private lateinit var navView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        viewPager = root.findViewById(R.id.mainViewPager)
        navView = root.findViewById(R.id.navView)
        setup()
        return root
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val page = when (item.itemId) {
            R.id.navigation_chart -> {
//                findNavController().navigate(R.id.action_mainFragment_to_navigation_chart)
                MainViewPagerAdapter.FRAGMENT_CHART
            }
            R.id.navigation_study -> {
//                findNavController().navigate(R.id.action_mainFragment_to_navigation_study)
                MainViewPagerAdapter.FRAGMENT_STUDIES
            }
            R.id.navigation_settings -> {
//                findNavController().navigate(R.id.action_mainFragment_to_navigation_settings)
                MainViewPagerAdapter.FRAGMENT_SETTINGS
            }
            else -> throw IllegalStateException()
        }
        viewPager.setCurrentItem(page, true)
        return true
    }

    private fun setup() {
        viewPager.apply {
            adapter = MainViewPagerAdapter(this@MainFragment)
            isUserInputEnabled = false
            currentItem = 1
        }
        navView.apply {
            selectedItemId = R.id.navigation_study
            setOnNavigationItemSelectedListener(this@MainFragment)
        }
    }
}
