package com.example.tofund_v3

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tofund_v3.databinding.ActivityDonationSuccessBinding
import com.example.tofund_v3.databinding.ActivityMainBinding

class donationSuccess : AppCompatActivity() {
    private lateinit var binding: ActivityDonationSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        binding = ActivityDonationSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backBtn = binding.backHomebutton

        backBtn.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}