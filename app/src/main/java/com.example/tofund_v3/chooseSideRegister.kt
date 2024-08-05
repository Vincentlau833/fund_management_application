package com.example.tofund_v3

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tofund_v3.databinding.ActivityChooseSideRegisterBinding

class chooseSideRegister : AppCompatActivity() {

    private lateinit var binding:ActivityChooseSideRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        binding = ActivityChooseSideRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val organizer = binding.organizerBtn
        val donor = binding.donorBtn

        organizer.setOnClickListener{
            val intent = Intent(this,organizerRegister::class.java)
            startActivity(intent)
        }

        donor.setOnClickListener{
            val intent2 = Intent(this,registerActivity::class.java)
            startActivity(intent2)
        }


    }
}