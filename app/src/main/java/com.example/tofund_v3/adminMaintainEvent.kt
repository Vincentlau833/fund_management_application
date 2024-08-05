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
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tofund_v3.databinding.FragmentAdminMaintainEventBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class adminMaintainEvent : Fragment() {

    private lateinit var binding: FragmentAdminMaintainEventBinding
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: adminEventAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminMaintainEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.adminLogout.setOnClickListener{

            val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            Toast.makeText(context,"Logout Successful",Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), login_activity::class.java)
            startActivity(intent)

        }

        //loading box
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Retrieve session
        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "")
        Log.d("sessionValueTest22", "login email = $userEmail")

        val dataList = mutableListOf<eventTable>()

        binding.adminResetSearchEvent.setOnClickListener{
            dataList.clear()
            db.collection("event").get().addOnSuccessListener { result ->
                progressDialog.dismiss()

                for (document in result) {
                    val eventData = eventTable(
                        EVENT_KEY = document.getString("EVENT_KEY") ?: "",
                        evName = document.getString("evName") ?: "",
                        condition = document.getString("condition")
                    )
                    dataList.add(eventData) // Add the instance to the dataList
                }
                // Display the data in the table layout
                displayData(dataList)
            }
        }

        binding.adminSearchEventBtn.setOnClickListener{
            val searchKey = binding.searchEvent.text.toString().trim().toUpperCase()
            db.collection("event").whereEqualTo("EVENT_KEY", searchKey).get().addOnSuccessListener { result ->
                progressDialog.dismiss()

                val dataList = mutableListOf<eventTable>()
                for (document in result) {
                    val eventData = eventTable(
                        EVENT_KEY = document.getString("EVENT_KEY") ?: "",
                        evName = document.getString("evName") ?: "",
                        condition = document.getString("condition")
                    )
                    dataList.add(eventData) // Add the instance to the dataList
                }
                // Display the data in the table layout
                displayData(dataList)
            }.addOnFailureListener { exception ->
                Log.d("search", "Error getting documents: ", exception)
            }
        }


        db.collection("event").get().addOnSuccessListener { result ->
                progressDialog.dismiss()

                for (document in result) {
                    val eventData = eventTable(
                        EVENT_KEY = document.getString("EVENT_KEY") ?: "",
                        evName = document.getString("evName") ?: "",
                        condition = document.getString("condition")
                    )
                    dataList.add(eventData) // Add the instance to the dataList
                }
                // Display the data in the table layout
                displayData(dataList)
            }
    }

    private fun displayData(dataList: List<eventTable>) {
        // Initialize the recycler view and adapter
        binding.adminEventRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = adminEventAdapter(dataList)
        binding.adminEventRecyclerView.adapter = adapter
    }
}