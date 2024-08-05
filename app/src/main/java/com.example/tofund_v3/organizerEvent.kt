package com.example.tofund_v3

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tofund_v3.databinding.FragmentOrganizerEventBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.FirestoreRecyclerOptions.Builder
import com.google.firebase.firestore.*
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.auth.User


class organizerEvent : Fragment() {
    private lateinit var binding:FragmentOrganizerEventBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var evArrayList:ArrayList<Event>
    private lateinit var evAdapter: eventListAdapter
    private lateinit var btnAdapter: eventListAdapter.OnClickListener
    private val db:FirebaseFirestore = FirebaseFirestore.getInstance()


    //Check for fragment attached
    private var isFragmentAttached = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isFragmentAttached = true
    }

    override fun onDetach() {
        super.onDetach()
        isFragmentAttached = false
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrganizerEventBinding.inflate(inflater, container, false)

        return binding.root
    }



//    Firestore Recycler Method

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //retreive session
        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "")
        Log.d("sessionValueTest2","login email = $userEmail")

        //recycler view
        recyclerView = view.findViewById(R.id.recyclerViewOrganizer)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        evArrayList = arrayListOf()
        evAdapter = eventListAdapter(evArrayList)

        recyclerView.adapter = evAdapter

        val addEvent = binding.addEventBtn

        eventListListener()


        addEvent.setOnClickListener{
            val fragmentAddEvent = organizerAddEvent()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.constraint_layout, fragmentAddEvent)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        evAdapter.setOnClickListener(object : eventListAdapter.OnClickListener {
            override fun onClick(position: Int, event: Event, eventKey:String) {

                val nextFragment = organizerEditEvent()
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()

                val bundle = Bundle()
                bundle.putString("eventKey", eventKey)
                nextFragment.arguments = bundle
                Log.d("forloop99", "$bundle")

                if(bundle != null){
                transaction.replace(R.id.constraint_layout, nextFragment)
                transaction.addToBackStack(null)
                transaction.commit()
                }
            }
        })
    }

    private fun eventListListener(){

        //loading box
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()
            if (isFragmentAttached) {
                //Toast.makeText(requireContext(),"Connection Times Out", Toast.LENGTH_SHORT).show()
            }
        }, 5000)

            db.collection("event").addSnapshotListener { value, error ->
                if (isFragmentAttached) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return@addSnapshotListener
                    }

                    // Retrieve session
                    val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
                    val userEmail = sharedPref.getString("userEmail", "")
                    Log.d("sessionValueTest2","login email = $userEmail")

                    db.collection("event")
                        .whereEqualTo("createdBy", userEmail)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (isFragmentAttached) {
                                for (dc in querySnapshot.documentChanges) {
                                    val createdBy = dc.document.getString("createdBy")

                                    Log.d("forloop1", "Retrieved document with createdBy=$createdBy")
                                    Log.d("forloop2", "Added event to evArrayList")
                                    if (dc.type == DocumentChange.Type.ADDED) {
                                        evArrayList.add(dc.document.toObject(Event::class.java))
                                    }
                                }

                                progressDialog.dismiss()

                                evAdapter.notifyDataSetChanged()
                            }
                        }
                        .addOnFailureListener { exception ->
                            if (isFragmentAttached) {
                                Log.d("FirestoreError", "Error getting documents: $exception")
                            }
                        }
                }
            }
        }

    }




