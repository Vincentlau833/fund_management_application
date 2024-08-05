package com.example.tofund_v3

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.tofund_v3.databinding.ActivityLoginBinding
import com.example.tofund_v3.databinding.ActivityRegisterBinding
import com.example.tofund_v3.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class resetPassword : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var auth: FirebaseAuth
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_reset_password)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = Firebase.auth
        val resetBtn = binding.resetBtn
        val cancel = binding.cancel

        cancel.setOnClickListener{
            val intentBack = Intent(this, login_activity::class.java)
            startActivity(intentBack)
        }


        resetBtn.setOnClickListener{

            val email = binding.resetEmail.text.toString()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            Log.d("reset", "Password reset email sent")
                            Toast.makeText(
                                this,
                                "Reset Link has been send to your email:$email",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.e("reset", "Failed to send password reset email", task.exception)
                            Toast.makeText(
                                this,
                                "Failed to send password reset email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Please Fill up the Email for password recovery",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }else{
                Toast.makeText(
                    this,
                    "Please Fill up the Email for password recovery",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }


}