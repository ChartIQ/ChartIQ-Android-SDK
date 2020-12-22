package com.chartiq.demo

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.ViewPumpAppCompatDelegate
import com.chartiq.demo.localization.LocalizationManager
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.MainFragment
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.chart.searchsymbol.VoiceQueryReceiver
import com.chartiq.sdk.ChartIQ


class MainActivity : AppCompatActivity(), MainFragment.MainFragmentPagerObserver {

    private val chartIQ: ChartIQ by lazy {
        (this.application as ServiceLocator).chartIQ
    }

    private val appPrefs by lazy {
        (this.application as ServiceLocator).applicationPreferences
    }

    private val mainViewModel: MainViewModel by viewModels(factoryProducer = {
        MainViewModel.ViewModelFactory(
            ChartIQNetworkManager(),
            appPrefs,
            chartIQ
        )
    })


    private val appCompatDelegate: AppCompatDelegate by lazy {
        ViewPumpAppCompatDelegate(
            baseDelegate = super.getDelegate(),
            baseContext = this,
            wrapContext = { baseContext -> LocalizationManager.wrapContext(baseContext) }
        )
    }

    override fun getDelegate(): AppCompatDelegate {
        return appCompatDelegate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel.currentLocaleEvent.observe({ lifecycle }, { event ->
            event.getContentIfNotHandled()?.let { translations ->
                LocalizationManager.updateTranslationsForLocale(
                    translations,
                    this,
                    findViewById<FrameLayout>(R.id.content)
                )
            }
        })
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

    override fun onDestroy() {
        super.onDestroy()
        // TODO: 03.12.20 There are situations where the system will simply kill
        //  the activity's hosting process without calling onDestroy
        //  Such a situation is swiping the app out of the recent tasks list
        //  clearSession() won't work in this situation which should be fixed
        //  side note: running a StickyService most likely won't work on api level 26+
        appPrefs.clearSession()
    }

    override fun onPageChanged() {
        Handler(Looper.getMainLooper()).postDelayed(
            { LocalizationManager.rewordUi(this.findViewById<FrameLayout>(R.id.content)) },
            400
        )
    }
}
