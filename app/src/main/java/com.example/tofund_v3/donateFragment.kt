package com.example.tofund_v3

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tofund_v3.databinding.FragmentDonateBinding
import com.google.firebase.firestore.*

class donateFragment : Fragment() {
    private lateinit var binding: FragmentDonateBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var evArrayList:ArrayList<Event>
    private lateinit var evAdapter: donorEventListAdaptter
    private lateinit var btnAdapter: eventListAdapter.OnClickListener
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDonateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewDonor)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        evArrayList = arrayListOf()
        evAdapter = donorEventListAdaptter(evArrayList)
        recyclerView.adapter = evAdapter

        eventListListener()

        evAdapter.setOnClickListenerDonor(object : donorEventListAdaptter.OnClickListenerDonor {
            override fun onClick(position: Int, event: Event, eventName:String, eventKey:String) {

                val nextFragment = donationDetailsFragment()
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()

                val bundle = Bundle()
                bundle.putString("eventKey", eventKey)
                bundle.putString("eventName", eventName)
                nextFragment.arguments = bundle
                Log.d("eventDonorChoose", "choose = $bundle")

                if(bundle != null){
                    transaction.replace(R.id.constraint_layout, nextFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
        })

        binding.button2.setOnClickListener{
            val fragmentBack = donateFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.constraint_layout, fragmentBack)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        binding.userSearchEventBtn.setOnClickListener{
            evArrayList.clear()
            //loading box
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Loading...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            var searchEventName = binding.searchEventName.text.toString()

            Toast.makeText(context,"$searchEventName",Toast.LENGTH_SHORT).show()
            db.collection("event")
                .whereEqualTo("evName", searchEventName)
                .whereEqualTo("condition", "active")
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                        if(error != null){
                            Log.e("Firestore Error", error.message.toString())
                            return
                        }

                        progressDialog.dismiss()
                        evArrayList.clear() // Clear the list before adding new items
                        for(dc : DocumentChange in value?.documentChanges!!){
                            if (dc.type == DocumentChange.Type.ADDED) {
                                evArrayList.add(dc.document.toObject(Event::class.java))
                            }
                        }
                        evAdapter.notifyDataSetChanged()
                    }
                })
        }

    }

    private fun eventListListener(){

        //retrieve user session
        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "").toString()
        Log.d("sessionValueTest2","login email = $userEmail")

        //loading box
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        db.collection("event")
            .whereEqualTo("condition", "active")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null){
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    progressDialog.dismiss()
                    evArrayList.clear() // Clear the list before adding new items
                    for(dc : DocumentChange in value?.documentChanges!!){
                        Log.d("forloop2", "Loaded event to evArrayList")
                        if (dc.type == DocumentChange.Type.ADDED) {
                            evArrayList.add(dc.document.toObject(Event::class.java))
                        }
                    }
                    evAdapter.notifyDataSetChanged()
                }
            })

    }

}