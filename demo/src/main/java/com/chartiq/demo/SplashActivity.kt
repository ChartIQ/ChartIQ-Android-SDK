package com.chartiq.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    private val job = Job()
    private val splashScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScope.launch {
            withContext(Dispatchers.IO) {
                delay(SPLASH_DURATION)
            }
            navigateToMainScreen()
        }
        setContentView(R.layout.activity_splash)
        super.onCreate(savedInstanceState)
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onPause() {
        job.cancel()
        super.onPause()
    }

    companion object {
        private const val SPLASH_DURATION = 1500L
    }
}
