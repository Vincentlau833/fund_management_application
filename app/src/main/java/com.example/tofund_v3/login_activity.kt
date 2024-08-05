package com.example.tofund_v3

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat.startActivity
import com.example.tofund_v3.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class login_activity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val loginBtn = binding.btnLogin
        val register = binding.btnRegister
        val forget = binding.forgetBtn

        val loginEmail = binding.editTextUserName.text.toString()
        val loginPwd = binding.editTextPassword.text.toString()

        loginBtn.setOnClickListener{
            performLogin()
        }

        register.setOnClickListener{
            val intent2 = Intent(this, chooseSideRegister::class.java)
            startActivity(intent2)
        }

        forget.setOnClickListener {
            val intent3 = Intent(this, resetPassword::class.java)
            startActivity(intent3)
        }


    }

    private fun performLogin() {
        val loginEmail = binding.editTextUserName.text.toString()
        val loginPwd = binding.editTextPassword.text.toString()

        if (loginEmail.isNotEmpty() && loginPwd.isNotEmpty()) {
            auth.signInWithEmailAndPassword(loginEmail, loginPwd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //store user email to session once login successful
                        val sharedPref = getSharedPreferences("loginSession", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("userEmail", loginEmail)
                        editor.apply()
                        db.collection("user").document(loginEmail).get()
                            .addOnSuccessListener { doc ->
                                val status = doc.getString("status")
                                if(status == "active") {


                                    db.collection("user").document(loginEmail).get()
                                        .addOnSuccessListener { topic ->
                                            val userTopic = topic.getString("topic")

                                            val topic = "/topics/$userTopic"
                                            FirebaseMessaging.getInstance().subscribeToTopic(topic)

                                        }

                                    //success Login
                                    val intent = Intent(this, MainActivity::class.java)
                                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                                    startActivity(intent)

                                    //retrieve user email from session
                                    val userEmail = sharedPref.getString("userEmail", "")
                                    Log.d("sessionValueTest", "login email = $userEmail")
                                }else {
                                    val builder = AlertDialog.Builder(this)
                                    builder.setTitle("Account Banned")
                                    builder.setCancelable(false)
                                    builder.setMessage("Your account has been banned by admin")
                                    builder.setPositiveButton("OK") { dialog, ok ->
                                        val editor = sharedPref.edit()
                                        editor.clear()
                                        editor.apply()

                                        val intent = Intent(this, login_activity::class.java)
                                        startActivity(intent)
                                    }
                                    val dialog = builder.create()
                                    dialog.show()

                                }

                            }

                    } else {
                        //fail login
                        Toast.makeText(baseContext, "Login Failed", Toast.LENGTH_SHORT).show()
                    }

                }
        } else {
            //Empty input validation
            Toast.makeText(baseContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private var backPressedTime: Long = 0
    private val PRESS_INTERVAL: Long = 1500 // 2 seconds

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