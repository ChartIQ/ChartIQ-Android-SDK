package com.chartiq.demo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chartiq.demo.ui.chart.ChartFragment
import com.chartiq.demo.ui.settings.SettingsFragment
import com.chartiq.demo.ui.study.StudyFragment

class MainViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = MainNavigation.values().size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            MainNavigation.FRAGMENT_CHART.value -> ChartFragment()
            MainNavigation.FRAGMENT_STUDIES.value -> StudyFragment()
            MainNavigation.FRAGMENT_SETTINGS.value -> SettingsFragment()
            else -> throw IllegalStateException()
        }
    }

    enum class MainNavigation(val value: Int) {
        FRAGMENT_CHART(0),
        FRAGMENT_STUDIES(1),
        FRAGMENT_SETTINGS(2)
    }
}
