package com.chartiq.demo

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: Toolbar
    private lateinit var viewPager: ViewPager2
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val page = when (item.itemId) {
            R.id.navigation_chart -> {
                toolbar.title = getString(R.string.title_chart)
                MainViewPagerAdapter.FRAGMENT_CHART
            }
            R.id.navigation_study -> {
                toolbar.title = getString(R.string.title_study)
                MainViewPagerAdapter.FRAGMENT_STUDIES
            }
            R.id.navigation_settings -> {
                toolbar.title = getString(R.string.title_settings)
                MainViewPagerAdapter.FRAGMENT_SETTINGS
            }
            else -> throw IllegalStateException()
        }
        viewPager.setCurrentItem(page, true)
        return true
    }

    private fun setup() {
        navView = findViewById(R.id.navView)
        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.mainViewPager)

        viewPager.apply {
            adapter = MainViewPagerAdapter(this@MainActivity)
            isUserInputEnabled = false
            currentItem = 1
        }
        navView.apply {
            selectedItemId = R.id.navigation_study
            setOnNavigationItemSelectedListener(this@MainActivity)
        }
    }
}
