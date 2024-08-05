package com.example.tofund_v3

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.tofund_v3.databinding.FragmentDonationDetailsBinding
import com.example.tofund_v3.databinding.FragmentSelectDonationDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text


class donationDetailsFragment : Fragment() {
    private lateinit var binding: FragmentDonationDetailsBinding
    private val db:FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDonationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)

        //loading box
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        //Receive bundle argument
        var eventKey = arguments?.getString("eventKey").toString()
        var eventName = arguments?.getString("eventName").toString()
        Log.d("eventname", "receive bundle $eventKey")

        //retrieve profile image from fire store
        val imageRef = FirebaseStorage.getInstance().getReference("eventPicture/${eventKey}EventPic.jpg")
        imageRef.downloadUrl.addOnSuccessListener { downloadUri->
            val imageUri = downloadUri.toString()

            Glide.with(this).load(imageUri).into(binding.eventImage)
        }


        binding.backButton.setOnClickListener{
            val fragmentBack = donateFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.constraint_layout, fragmentBack)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        //retrieve data base on event Key
        db.collection("event").document(eventKey).get().addOnSuccessListener { document->
            progressDialog.dismiss()
            if(document != null){
                binding.eventName.text = document.getString("evName")
                binding.eventID.text = document.getString("EVENT_KEY")
                binding.description.text = document.getString("evDescription")

                val amount = document.getLong("evTargetAmount").toString()

                db.collection("donation").document(eventKey).get().addOnSuccessListener { document ->
                    var collectedAmount = document.getLong("evCollectedAmount").toString()
                    binding.target.text = ("$collectedAmount / $amount")

                    if(collectedAmount == amount){

                        binding.donateBtn.setOnClickListener{
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Fund Event Completed")
                            builder.setMessage("This Fund Event has reach their targeted amount")
                            builder.setPositiveButton("OK") { dialog, dialog1 ->
                                dialog.dismiss()
                            }
                            builder.show()
                        }


                        binding.donateBtn.text = "Event Completed"

                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Fund Event Completed")
                        builder.setMessage("This Fund Event has reach their targeted amount")
                        builder.setPositiveButton("OK") { dialog, dialog2 ->
                            dialog.dismiss()
                        }
                        builder.show()
                    }
                }
            }else{
                Toast.makeText(context,"Event not found",Toast.LENGTH_SHORT).show()
            }
        }

        var donateBtn = binding.donateBtn

        donateBtn.setOnClickListener{
            val nextFragment = selectDonationDetails()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            //set bundle to pass event ID & key to other fragment
            val bundleDonate = Bundle()
            bundleDonate.putString("key",eventKey)
            bundleDonate.putString("eventName", eventName)
            nextFragment.arguments = bundleDonate

            if(bundleDonate != null) {
                transaction.replace(R.id.constraint_layout, nextFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }
}