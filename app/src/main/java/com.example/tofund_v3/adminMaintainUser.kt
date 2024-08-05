package com.example.tofund_v3

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tofund_v3.databinding.FragmentAdminMaintainUserBinding
import com.google.firebase.firestore.FirebaseFirestore

class adminMaintainUser : Fragment() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentAdminMaintainUserBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminMaintainUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //loading box
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()



        binding.adminResetSearchUserBtn.setOnClickListener{
            val dataList = mutableListOf<AdminUser>()

            db.collection("user").get().addOnSuccessListener { result ->
                progressDialog.dismiss()

                for (document in result) {
                    val email = document.getString("email")
                    if (email == "admin@gmail.com") {
                        continue
                    }

                    val userData = AdminUser(
                        email = email ?: "",
                        username = document.getString("name") ?: "",
                        role = document.getString("role") ?: "",
                        status = document.getString("status")
                    )
                    dataList.add(userData)
                }
                displayData(dataList)
            }

        }

        binding.adminSearchUserBtn.setOnClickListener{
            val searchEmail = binding.searchUser.text.trim().toString()
            db.collection("user").get().addOnSuccessListener { result ->
                progressDialog.dismiss()

                val dataList = mutableListOf<AdminUser>()
                for (document in result) {
                    val email = document.getString("email")
                    if (email == "admin@gmail.com") {
                        continue
                    }
                    if (email == searchEmail) {
                        val userData = AdminUser(
                            email = email ?: "",
                            username = document.getString("name") ?: "",
                            role = document.getString("role") ?: "",
                            status = document.getString("status")
                        )
                        dataList.add(userData)
                    }
                }
                displayData(dataList)
            }
        }


        binding.adminUserLogout.setOnClickListener {

            val sharedPref =
                requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            Toast.makeText(context,"Logout Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), login_activity::class.java)
            startActivity(intent)

        }



        // Retrieve session
        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "")
        Log.d("sessionValueTest22", "login email = $userEmail")

        val dataList = mutableListOf<AdminUser>()

        db.collection("user").get().addOnSuccessListener { result ->
            progressDialog.dismiss()

            for (document in result) {
                val email = document.getString("email")
                if (email == "admin@gmail.com") {

                    continue
                }

                val userData = AdminUser(
                    email = email ?: "",
                    username = document.getString("name") ?: "",
                    role = document.getString("role") ?: "",
                    status = document.getString("status")
                )
                dataList.add(userData)
            }
            displayData(dataList)
        }



    }

    private fun displayData(dataList: List<AdminUser>) {
        // Initialize the recycler view and adapter
        binding.adminUserRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = adminUserAdapter(dataList)
        binding.adminUserRecyclerView.adapter = adapter
    }
}