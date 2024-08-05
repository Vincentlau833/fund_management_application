package com.example.tofund_v3

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class startApp : AppCompatActivity() {
    //app logo display for 1 second
    private val SPLASH_DURATION: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_start_app)

        Handler().postDelayed({
            val intent = Intent(this@startApp, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DURATION)
    }
}