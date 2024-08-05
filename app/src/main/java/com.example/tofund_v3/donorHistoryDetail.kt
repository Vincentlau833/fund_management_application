package com.example.tofund_v3

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import com.example.tofund_v3.databinding.FragmentDonorHistoryDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class donorHistoryDetail : Fragment() {
    private lateinit var binding: FragmentDonorHistoryDetailBinding
    private val db:FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDonorHistoryDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //loading box
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        binding.backButton.setOnClickListener{
            val fragmentBack = userDonateHistory()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.constraint_layout, fragmentBack)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "")

        //Receive bundle argument
        var donorName = arguments?.getString("donorName").toString()
        var eventName = arguments?.getString("eventName").toString()
        var eventKey = arguments?.getString("eventKey").toString()
        Log.d("retrieve event detail","$eventName $donorName $eventKey")


        var donateDetailRef = db.collection("donorList").document(eventKey).
        collection(userEmail.toString()).document(donorName).get().addOnSuccessListener { historyDetail ->

            progressDialog.dismiss()

            val eventKey = historyDetail.getString("EVENT_KEY")
            val eventName = historyDetail.getString("eventName")
            val donorName = historyDetail.getString("donorName")
            val amount = historyDetail.getLong("donateAmount").toString()
            val time = historyDetail.getString("donateDateTime")

            binding.donateDetailKey.text = eventKey
            binding.donateDetailName.text = donorName
            binding.donateDetailAmount.text = amount
            binding.donateDetailTime.text = time
            binding.donateDetailEventName.text = eventName

        }
    }
}