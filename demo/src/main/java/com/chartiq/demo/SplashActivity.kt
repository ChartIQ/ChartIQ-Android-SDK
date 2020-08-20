package com.chartiq.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SplashActivity: AppCompatActivity() {

    private val job = SupervisorJob()
    private val splashScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScope.launch {
            withContext(Dispatchers.IO) {
                delay(1500L)
            }
            navigateToMainScreen()
        }
        setContentView(R.layout.activity_splash)
        super.onCreate(savedInstanceState)
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onPause() {
        job.cancelChildren()
        super.onPause()
    }
}