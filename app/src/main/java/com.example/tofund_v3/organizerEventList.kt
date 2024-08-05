package com.example.tofund_v3

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tofund_v3.databinding.FragmentOrganizerEventListBinding
import com.google.firebase.firestore.FirebaseFirestore


class organizerEventList : Fragment() {
    private lateinit var binding: FragmentOrganizerEventListBinding
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrganizerEventListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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


        db.collection("donation").document()

        db.collection("event")
            .whereEqualTo("createdBy", userEmail).get().addOnSuccessListener { result ->
                progressDialog.dismiss()

            for (document in result) {
                val eventData = eventTable(
                    EVENT_KEY = document.getString("EVENT_KEY") ?: "",
                    evName = document.getString("evName") ?: "",
                    evCollectedAmount = document.getLong("evCollectedAmount"),
                    evTargetAmount = document.getLong("evTargetAmount"),
                )
                dataList.add(eventData) // Add the instance to the dataList
            }
            // Display the data in the table layout
            displayData(dataList)
        }
    }

    private fun displayData(dataList: List<eventTable>) {
        // Initialize the recycler view and adapter
        binding.tableEventRecyclerview.layoutManager = LinearLayoutManager(activity)
        val adapter = eventTableAdapter(dataList)
        binding.tableEventRecyclerview.adapter = adapter
    }




}