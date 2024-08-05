package com.example.tofund_v3

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tofund_v3.databinding.ActivityOrganizerRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class organizerRegister : AppCompatActivity() {
    private lateinit var binding:ActivityOrganizerRegisterBinding
    private var db:FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var authOr: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityOrganizerRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize Firebase Auth
        authOr = Firebase.auth

        val organizerRegister = binding.organizerRegister
        val loginBack = binding.organizerBackLogin

        organizerRegister.setOnClickListener{
            registerOrganizer()
        }

        loginBack.setOnClickListener{
            val intent = Intent(this,login_activity::class.java)
            startActivity(intent)
        }
    }

    private fun registerOrganizer() {
        val name = binding.organizerName.text.toString()
        val email = binding.OrganizerEmail.text.toString()
        val password = binding.organizerPwd.text.toString()
        val confirmPassword = binding.organizerConfirmPwd.text.toString()
        val role = "Charity Organizer"

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all Information", Toast.LENGTH_SHORT).show()
            return
        }

        if (name.contains(".") || name.contains("/")) {
            Toast.makeText(this, "User name should not contains . or / ", Toast.LENGTH_SHORT).show()
            return
        }

        val topic = name.replace("\\s".toRegex(), "")

        val data = hashMapOf(
            "email" to email,
            "name" to name,
            "topic" to topic,
            "role" to role,
            "status" to "active"
        )

        if (password.compareTo(confirmPassword) == 0){

            authOr.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val registerSuccessCharity = authOr.currentUser

                        //pass organizer data from authentication to fire store
                        db.collection("user").document("${email}").set(data)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    baseContext,
                                    "Register Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        //register success


                        //register success
                        Toast.makeText(baseContext, "Register Success", Toast.LENGTH_SHORT).show()
                        val intent2 = Intent(this, login_activity::class.java)

                        startActivity(intent2)

                    } else {
                        //register fail
                        Toast.makeText(baseContext, "Register Failed", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                Toast.makeText(this, "Register Failed ${it.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
    }else{
            Toast.makeText(this, "Password not match with confirm password", Toast.LENGTH_SHORT).show()
        }
    }
}