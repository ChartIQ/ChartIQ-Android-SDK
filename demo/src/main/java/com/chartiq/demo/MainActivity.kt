package com.chartiq.demo

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.chartiq.demo.ui.chart.searchsymbol.VoiceQueryReceiver

class MainActivity : AppCompatActivity() {

    private val appPrefs by lazy {
        ApplicationPrefs.Default(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
                else -> Unit
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
