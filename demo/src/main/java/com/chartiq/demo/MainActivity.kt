package com.chartiq.demo

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.ViewPumpAppCompatDelegate
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.chart.searchsymbol.VoiceQueryReceiver
import com.chartiq.sdk.ChartIQ
import dev.b3nedikt.restring.Restring
import dev.b3nedikt.reword.Reword

class MainActivity : AppCompatActivity() {

    private val chartIQ: ChartIQ by lazy {
        (this.application as ChartIQApplication).chartIQ
    }

    private val mainViewModel: MainViewModel by viewModels(factoryProducer = {
        MainViewModel.ViewModelFactory(
            ChartIQNetworkManager(),
            ApplicationPrefs.Default(this),
            chartIQ
        )
    })

    private val appPrefs by lazy {
        ApplicationPrefs.Default(this)
    }

    private val appCompatDelegate: AppCompatDelegate by lazy {
        ViewPumpAppCompatDelegate(
            baseDelegate = super.getDelegate(),
            baseContext = this,
            wrapContext = { baseContext -> Restring.wrapContext(baseContext) }
        )
    }

    override fun getDelegate(): AppCompatDelegate {
        return appCompatDelegate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel.newLocaleEvent.observe({ lifecycle }, { event ->
            event.getContentIfNotHandled()?.let { translations ->
                val currentStrings: Map<String, ResourceTranslation> = getDefaultStrings()
                val newTranslations: List<ResourceTranslation> = currentStrings.map {
                    ResourceTranslation(
                        it.value.resourceKey,
                        translations.values[it.key] ?: it.value.resourceValue
                    )
                }
                Restring.putStrings(
                    translations.locale,
                    newTranslations.map { it.resourceKey to it.resourceValue }.toMap()
                )
                Restring.locale = translations.locale
                val rootView = window.decorView.findViewById<FrameLayout>(R.id.content)
                Reword.reword(rootView)
            }
        })
    }

    private fun getDefaultStrings(): Map<String, ResourceTranslation> {
        return R.string::class.java.fields
            .map {
                val resValue = resources.getString(it.getInt(null))
                ResourceTranslation(resourceKey = it.name, resourceValue = resValue)
            }.associateBy { it.resourceValue }
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
}
