//package com.example.tofund_v3
//
//import android.content.Context
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.tofund_v3.databinding.FragmentDonationHistoryBinding
//import com.google.firebase.firestore.FirebaseFirestore
//
//
//class donation_History : Fragment() {
//    private lateinit var binding: FragmentDonationHistoryBinding
//    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentDonationHistoryBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        Log.d("donationHistory", "No documents found")
//
//        // Retrieve session
//        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
//        val userEmail = sharedPref.getString("userEmail", "")
//        Log.d("sessionValueTest22", "login email = $userEmail")
//
//
//
//        val historyList = mutableListOf<donationHistory>()
//
//        db.collection("donorList").get().addOnSuccessListener { querySnapshot ->
//            Log.d("donationHistory", "Query snapshot is empty: " + querySnapshot.isEmpty)
//            if(querySnapshot.isEmpty){
//                Log.d("donationHistory", "No documents found")
//            } else {
//                Log.d("donationHistory", "Documents found")
//            }
//
//            for (document in querySnapshot.documents) {
//                val eventID = document.id
//                val donorEmailsRef = db.collection("donorList").document(eventID).collection(userEmail.toString())
//                Log.d("donorRef","$donorEmailsRef")
//
//                donorEmailsRef.get().addOnSuccessListener { donorEmail ->
//                    for (donorDoc in donorEmail.documents) {
//                        val donorName = donorDoc.id
//                        val donationAmount = donorDoc.getLong("donateAmount")
//
//                        val historyData = donationHistory(
//                            EVENT_KEY = document.getString("EVENT_KEY") ?: "",
//                            donateDateTime = document.getString("donateDateTime") ?: "",
//                            donorName = document.getString("donorName") ?: "",
//                            donateAmount = document.getLong("donateAmount").toString().toInt()
//                        )
//                        historyList.add(historyData)
//
//                        //debug
//                        Log.d("donorname","$donorName")
//                    }
//
//                    // Debug - ensure all data has been added to historyList
//                    Log.d("historyList", "$historyList")
//
//                    // Check if historyList has been completely populated
//                    if (historyList.size == querySnapshot.documents.size * donorEmail.documents.size) {
//                        // Display the data in the table layout
//                        if (historyList.isNotEmpty()) {
//                            displayData(historyList)
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//    private fun debug(){
//        Log.d("donationHistory", "No documents found")
//    }
//
//    private fun displayData(historyList: List<donationHistory>) {
//        // Initialize the recycler view and adapter
//        binding.historyRecyclerView2.layoutManager = LinearLayoutManager(activity)
//        binding.historyRecyclerView2.adapter= historyAdapter(historyList)
//
//    }
//}
//
