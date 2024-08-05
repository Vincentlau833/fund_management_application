package com.example.tofund_v3


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    //bottom nav variable
    private lateinit var bottomNav:BottomNavigationView
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //retreive session
        val sharedPref = getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        var email = sharedPref.getString("userEmail", "").toString()
        //val userEmail = "donor123@gmail.com"
        Log.d("sessionValueTest2", "login email = $email")


        if (email == "") {
            val intent = Intent(this, login_activity::class.java)
            startActivity(intent)
        } else {
            db.collection("user").document(email).get().addOnSuccessListener { status ->
                val userStatus = status.getString("status")
                if (userStatus == "active") {

                    getRole(email) { role ->

                        if (email == "") {
                            Log.d("Login Test", "User email is $email")
                        } else {


                            //set default fragment as home fragment
                            replaceFragment(homeFragment())
                            bottomNav = findViewById(R.id.bottom_nav)
                            bottomNav.background = null

                            if (role == "donor") {
                                Log.d("roleTest", "role is $role")
                                bottomNav.setOnItemSelectedListener {
                                    when (it.itemId) {
                                        R.id.homeFragment -> replaceFragment(homeFragment())
                                        R.id.donateFragment -> replaceFragment(donateFragment())
                                        R.id.paymentFragment -> replaceFragment(userDonateHistory())
                                        R.id.profileFragment -> replaceFragment(profileFragment())
                                    }
                                    true
                                }
                            } else if (role == "Charity Organizer") {
                                Log.d("roleTest", "role is $role")
                                bottomNav.setOnItemSelectedListener {
                                    when (it.itemId) {
                                        R.id.homeFragment -> replaceFragment(homeFragment())
                                        R.id.donateFragment -> replaceFragment(organizerEvent())
                                        R.id.paymentFragment -> replaceFragment(organizerEventList())
                                        R.id.profileFragment -> replaceFragment(profileFragment())
                                    }
                                    true
                                }
                            } else {
                                val adminIntent = Intent(this, admin::class.java)
                                startActivity(adminIntent)
                            }
                        }

                    }
                }else{
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
    }

    }

    private fun getRole(userEmail: String, callback: (String?) -> Unit) {
        val docRef = db.collection("user").document(userEmail)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val role = document.getString("role")
                    Log.d("role", "Role: $role")
                    callback(role)
                } else {
                    Log.d(TAG, "No such document")
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                callback(null)
            }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.constraint_layout, fragment)
        fragmentTransaction.commit()
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