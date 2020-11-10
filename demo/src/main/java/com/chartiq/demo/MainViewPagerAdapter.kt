package com.chartiq.demo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chartiq.demo.ui.chart.ChartFragment
import com.chartiq.demo.ui.settings.SettingsFragment
import com.chartiq.demo.ui.study.StudyFragment

class MainViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = listOf(
        FRAGMENT_CHART,
        FRAGMENT_STUDIES,
        FRAGMENT_SETTINGS
    ).size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FRAGMENT_CHART -> ChartFragment()
            FRAGMENT_STUDIES -> StudyFragment()
            FRAGMENT_SETTINGS -> SettingsFragment()
            else -> throw IllegalStateException()
        }
    }

    companion object {
        const val FRAGMENT_CHART = 0
        const val FRAGMENT_STUDIES = 1
        const val FRAGMENT_SETTINGS = 2
    }
}
