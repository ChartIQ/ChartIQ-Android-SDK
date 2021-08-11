package com.chartiq.demo.ui

import android.content.Context
import android.content.res.Configuration
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
import com.chartiq.demo.ui.chart.ChartViewState
import com.chartiq.sdk.ChartIQ
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
            val instantScroll = page == MainViewPagerAdapter.MainNavigation.FRAGMENT_CHART.value &&
                    resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            binding.mainViewPager.setCurrentItem(page, !instantScroll)
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


    private fun setupViews() {
        with(binding) {
            mainViewPager.apply {
                adapter = MainViewPagerAdapter(this@MainFragment)
                isUserInputEnabled = false
                currentItem = MainViewPagerAdapter.MainNavigation.FRAGMENT_CHART.ordinal
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        if (position == MainViewPagerAdapter.MainNavigation.FRAGMENT_STUDIES.ordinal) {
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
        mainViewModel.chartViewState.observe(viewLifecycleOwner) { state ->
            binding.navView.isVisible = state is ChartViewState.Simple
        }
        mainViewModel.networkIsAvailableEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { value ->
                if (!value) {
                    showDeviceIsOfflineDialog()
                }
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


    interface MainFragmentPagerObserver {
        fun onPageChanged()
    }
}
