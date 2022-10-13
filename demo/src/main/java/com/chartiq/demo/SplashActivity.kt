package com.chartiq.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chartiq.demo.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    private val job = SupervisorJob()
    private val splashScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_splash)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        splashScope.launch {
            withContext(Dispatchers.IO) {
                delay(SPLASH_DURATION)
            }
            navigateToMainScreen()
        }
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onPause() {
        job.cancelChildren()
        super.onPause()
    }

    companion object {
        private const val SPLASH_DURATION = 1500L
    }
}
