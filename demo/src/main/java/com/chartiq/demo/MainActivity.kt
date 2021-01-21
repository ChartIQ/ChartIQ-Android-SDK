package com.chartiq.demo

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.ViewPumpAppCompatDelegate
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.MainFragment
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.chart.searchsymbol.VoiceQueryReceiver
import com.chartiq.sdk.ChartIQ
import dev.b3nedikt.restring.Restring


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

        if(android.os.Build.MANUFACTURER == "Xiaomi") {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = 0x66000000
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            when (it.action) {
                Intent.ACTION_SEARCH -> {
                    intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                        (supportFragmentManager
                            .findFragmentById(R.id.nav_host_fragment)
                            ?.childFragmentManager
                            ?.fragments?.getOrNull(0) as? VoiceQueryReceiver)
                            ?.receiveVoiceQuery(query)
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
}
