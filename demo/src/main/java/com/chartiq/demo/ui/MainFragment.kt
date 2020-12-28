package com.chartiq.demo.ui

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.MainViewPagerAdapter
import com.chartiq.demo.R
import com.chartiq.demo.ServiceLocator
import com.chartiq.demo.databinding.FragmentMainBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.ChartTheme
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private val chartIQ: ChartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }

    private val mainViewModel: MainViewModel by activityViewModels(factoryProducer = {
        MainViewModel.ViewModelFactory(
            ChartIQNetworkManager(),
            (requireActivity().application as ServiceLocator).applicationPreferences,
            chartIQ,
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
        mainViewModel.checkInternetAvailability()

        setupViews()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(isDarkThemeOn()) {
            mainViewModel.updateTheme(ChartTheme.NIGHT)
        } else {
            mainViewModel.updateTheme(ChartTheme.DAY)
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
                            (requireActivity() as MainFragmentPagerObserver).onPageChanged()
                        }
                    }
                })
            }
            navView.apply {
                selectedItemId = R.id.navigation_chart
                setOnNavigationItemSelectedListener(onNavItemSelectedListener)
            }
        }
        mainViewModel.isNavBarVisible.observe(viewLifecycleOwner) { isVisible ->
            binding.navView.isVisible = isVisible
        }
        mainViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) {
                reloadData()
            } else {
                showDeviceIsOfflineDialog()
            }
        }
    }

    private fun showDeviceIsOfflineDialog() {
        AlertDialog.Builder(requireContext(), R.style.PositiveAlertDialogTheme)
            .setTitle(R.string.general_warning_something_went_wrong)
            .setMessage(R.string.general_the_internet_connection_appears_to_be_offline)
            .setNegativeButton(R.string.general_cancel) { _, _ -> Unit }
            .setPositiveButton(R.string.general_reconnect) { _, _ ->
                mainViewModel.checkInternetAvailability()
            }
            .create()
            .apply {
                setCanceledOnTouchOutside(false)
            }
            .show()
    }

    private fun reloadData() {
        mainViewModel.setupChart()
        mainViewModel.fetchActiveStudyData()
    }

    private fun isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    interface MainFragmentPagerObserver {
        fun onPageChanged()
    }
}
