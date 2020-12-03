package com.chartiq.demo.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    private val chartIQ: ChartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }

    private val mainViewModel: MainViewModel by activityViewModels(factoryProducer = {
        MainViewModel.ViewModelFactory(
            ChartIQNetworkManager(),
            ApplicationPrefs.Default(requireContext()),
            chartIQ
        )
    })

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

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable()) {
            reloadData()
        } else {
            Toast.makeText(requireContext(), "The Internet is not available", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setupViews() {
        with(binding) {
            mainViewPager.apply {
                adapter = MainViewPagerAdapter(this@MainFragment)
                isUserInputEnabled = false
                currentItem = MainViewPagerAdapter.MainNavigation.FRAGMENT_CHART.ordinal
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        if (position == MainViewPagerAdapter.MainNavigation.FRAGMENT_STUDIES.ordinal) {
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

    private fun reloadData() {
        mainViewModel.fetchActiveStudyData()
    }

    private fun isNetworkAvailable(): Boolean {
        return (requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).let { connectivityManager ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                return connectivityManager.activeNetworkInfo != null
            } else {
                val allNetworks: Array<Network> = connectivityManager.allNetworks
                for (network in allNetworks) {
                    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                    if (networkCapabilities != null) {
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                        ) {
                            return true
                        }
                    }
                }
            }
            return false
        }
    }
}
