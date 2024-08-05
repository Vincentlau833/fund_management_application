package com.example.tofund_v3

import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tofund_v3.databinding.ActivityAdminBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class admin : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tablayout = binding.tabLayoutAdmin1
        val viewPager2 = binding.tabsViewpager2

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tablayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Event"
                }
                1 -> {
                    tab.text = "User"
                }

            }
        }.attach()
    }

    private var backPressedTime: Long = 0
    private val PRESS_INTERVAL: Long = 2000 // 2 seconds

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - backPressedTime < PRESS_INTERVAL) {
            // Pressed twice within the interval, exit the app
            finishAffinity()
        } else {
            backPressedTime = currentTime
            Toast.makeText(this, "Press again to exit ToFund", Toast.LENGTH_SHORT).show()
        }
    }
}
