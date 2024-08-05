package com.example.tofund_v3

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tofund_v3.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class registerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize Firebase Auth
        auth = Firebase.auth

        val registerBtn = binding.registerBtn
        val backLoginBtn = binding.backLoginDonor

        registerBtn.setOnClickListener{

            registerFun()
        }

        backLoginBtn.setOnClickListener{
            val intent = Intent(this,login_activity::class.java)
            startActivity(intent)
        }
    }

    private fun registerFun() {
        val email = binding.editTextUseremail.text.toString()
        val name = binding.editTextUsername.text.toString()
        val password = binding.userPassword.text.toString()
        val confirmPassword = binding.userConfirmPassword.text.toString()
        val role = "donor"


        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all Information", Toast.LENGTH_SHORT).show()
            return
        }


        val data = hashMapOf(
            "email" to email,
            "name" to name,
            "role" to role,
            "topic" to name,
            "status" to "active"
        )

        if (password.compareTo(confirmPassword) == 0) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val registerSuccessUser = auth.currentUser

                        //pass user data from authentication to fire store
                        db.collection("user").document("${email}").set(data)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    baseContext,
                                    "Register Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        //register success
                        Toast.makeText(baseContext, "Register Success", Toast.LENGTH_SHORT).show()
                        val intent2 = Intent(this, login_activity::class.java)
                        startActivity(intent2)
                    } else {
                        //register fail
                        Toast.makeText(baseContext, "Register Failed", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Register Failed ${it.localizedMessage}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }
    }
}