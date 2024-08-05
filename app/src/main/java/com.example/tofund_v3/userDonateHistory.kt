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
import com.example.tofund_v3.databinding.FragmentUserDonateHistoryBinding
import com.google.firebase.firestore.FirebaseFirestore


class userDonateHistory : Fragment() {
    private lateinit var binding: FragmentUserDonateHistoryBinding
    private val db:FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserDonateHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("donationHistory", "No documents found")

        // Retrieve session
        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "")
        Log.d("sessionValueTest22", "login email = $userEmail")

        //loading box
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val historyList = mutableListOf<donationHistory>()

        db.collection("donorList").get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val eventID = document.id
                val donorEmailsRef = db.collection("donorList").document(eventID).collection(userEmail.toString())

                donorEmailsRef.get().addOnSuccessListener { donorEmail ->
                    progressDialog.dismiss()
                    for (donorDoc in donorEmail.documents) {
                        val donorName = donorDoc.id
                        val donateDateTime = donorDoc.getString("donateDateTime")
                        val donateAmount = donorDoc.getLong("donateAmount").toString()
                        val eventName = donorDoc.getString("eventName").toString()

                        val historyData = donationHistory(
                            EVENT_KEY = document.getString("EVENT_KEY") ?: "",
                            donateDateTime = donateDateTime,
                            donorName = donorName,
                            donateAmount = donateAmount,
                            eventName = eventName
                        )
                        historyList.add(historyData)
                    }

                    // Debug, ensure all data has been added to historyList
                    Log.d("historyList", "$historyList")

                    // Display the data in the table layout
                    if (historyList.isNotEmpty()) {
                        displayData(historyList)
                    }
                }
            }
        }
    }

    private fun displayData(historyList: List<donationHistory>) {
        // Initialize the recycler view and adapter
        binding.historyRecyclerView2.layoutManager = LinearLayoutManager(activity)
        binding.historyRecyclerView2.adapter= historyAdapter(historyList)

    }
}