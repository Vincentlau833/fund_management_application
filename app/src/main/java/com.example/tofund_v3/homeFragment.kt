package com.example.tofund_v3

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tofund_v3.databinding.ActivityMainBinding
import com.example.tofund_v3.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore


class homeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var db:FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //retrieve user session
        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "").toString()
        Log.d("sessionValueTest2","login email = $userEmail")

        var eventBtn = binding.homeDonateBtn

        db.collection("user").document(userEmail).get().addOnSuccessListener { role->
            val role = role.getString("role")

            if(role == "donor"){
                eventBtn.text = "Donate Some Now"
                eventBtn.setOnClickListener {
                    val fragmentDonate = donateFragment()
                    val fragmentManager = requireActivity().supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.constraint_layout, fragmentDonate)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }else if(role == "Charity Organizer"){
                eventBtn.text = "Create Event Now"
                eventBtn.setOnClickListener {
                    val fragmentDonate = organizerAddEvent()
                    val fragmentManager = requireActivity().supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.constraint_layout, fragmentDonate)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }

        }


    }
}
