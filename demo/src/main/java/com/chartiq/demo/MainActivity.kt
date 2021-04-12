package com.chartiq.demo

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.ViewPumpAppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.MainFragment
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.chart.searchsymbol.ChooseSymbolFragment
import com.chartiq.demo.ui.chart.searchsymbol.VoiceQueryReceiver
import com.chartiq.sdk.ChartIQ
import dev.b3nedikt.restring.Restring
import java.lang.NullPointerException


class MainActivity : AppCompatActivity(), MainFragment.MainFragmentPagerObserver {

    private val chartIQ: ChartIQ by lazy {
        (application as ServiceLocator).chartIQ
    }
    private val localizationManager by lazy {
        (application as ServiceLocator).localizationManager
    }
    private val mainViewModel: MainViewModel by viewModels(factoryProducer = {
        MainViewModel.ViewModelFactory(
                ChartIQNetworkManager(),
                (application as ServiceLocator).applicationPreferences,
                chartIQ,
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        )
    })
    private val appCompatDelegate: AppCompatDelegate by lazy {
        ViewPumpAppCompatDelegate(
                baseDelegate = super.getDelegate(),
                baseContext = this,
                wrapContext = { baseContext ->
                    Restring.wrapContext(baseContext)
                }
        )
    }

    override fun getDelegate(): AppCompatDelegate {
        return appCompatDelegate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel.prepareSession()

        mainViewModel.currentLocaleEvent.observe({ lifecycle }, { event ->
            event.getContentIfNotHandled()?.let { translations ->
                localizationManager.updateTranslationsForLocale(
                        translations,
                        findViewById<FrameLayout>(R.id.content)
                )
            }
        })
        mainViewModel.setFullscreen(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)

        if (android.os.Build.MANUFACTURER == "Xiaomi") {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = 0x66000000
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        mainViewModel.setFullscreen(isLandscape)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            when (it.action) {
                Intent.ACTION_SEARCH -> {
                    intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                        try {
                            val voiceReceiver = findNestedDialogFragment(
                                    supportFragmentManager,
                                    ChooseSymbolFragment.DIALOG_TAG
                            ) as? VoiceQueryReceiver

                            voiceReceiver?.receiveVoiceQuery(query)
                        } catch (e: NullPointerException) {
                            Log.d(this.javaClass.name, "No voice receiver found")
                        }
                    }
                }
                else -> {
                    Unit
                }
            }
        }
    }

    override fun onPageChanged() {
        Handler(Looper.getMainLooper()).postDelayed(
                { localizationManager.rewordUi(this.findViewById<FrameLayout>(R.id.content)) },
                400
        )
    }

    private fun findNestedDialogFragment(manager: FragmentManager, tag: String): Fragment? {
        val fragment = manager.findFragmentByTag(tag)
        return if (fragment == null) {
            val fragmentManager = manager.fragments.getOrElse(0) {
                return null
            }.childFragmentManager
            findNestedDialogFragment(fragmentManager, tag)
        } else {
            fragment
        }
    }
}
