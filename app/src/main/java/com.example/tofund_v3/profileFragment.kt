package com.example.tofund_v3

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.tofund_v3.databinding.FragmentProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class profileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val EDIT_PROFILE_REQUEST_CODE = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root


    }
    override fun onResume() {
        super.onResume()

        //loading box
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "")
        Log.d("sessionValueTest2", "login email = $userEmail")

        //Retreive data from firestore
        val docRef = db.collection("user").document(userEmail.toString())

        docRef.get()
            .addOnSuccessListener { document ->
                progressDialog.dismiss()

                if (document != null) {
                    //retrieve profile image from fire store
                    try {
                        val imageRef = FirebaseStorage.getInstance().getReference("profilePicture/${userEmail}ProfilePic.jpg")
                        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            val imageUri = downloadUri.toString()
                            context?.let {
                                try {
                                    if (isAdded && view != null) {
                                        Glide.with(this)
                                            .load(imageUri)
                                            .into(binding.imageView5)
                                    } else {
                                        Toast.makeText(context, "Please Try Again Later", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: NullPointerException){
                                    Log.e(TAG, "Error loading profile picture", e)
                                    Toast.makeText(context, "Please Try Again Later", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } catch (e: NullPointerException) {
                        Log.e(TAG, "Error loading profile picture", e)
                        Toast.makeText(context, "Please Try Again Later", Toast.LENGTH_SHORT).show()
                    }




                    val emailField = document.getString("email")
                    val nameField = document.getString("name")
                    val phoneField = document.getString("phone")
                    val role = document.getString("role")

                    binding.username.text = nameField.toString()
                    binding.email.text = emailField.toString()
                    binding.userPhone.text = phoneField.toString()

                } else {
                    Toast.makeText(context, "No record found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //loading box
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()


        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "")
        Log.d("sessionValueTest2", "login email = $userEmail")

        //Retreive data from firestore
        val docRef = db.collection("user").document(userEmail.toString())

        docRef.get()
            .addOnSuccessListener { document ->
                progressDialog.dismiss()

                if (document != null) {
                    //retrieve profile image from fire store
                    val imageRef = FirebaseStorage.getInstance().getReference("profilePicture/${userEmail}ProfilePic.jpg")
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri->
                        val imageUri = downloadUri.toString()

                        Glide.with(this).load(imageUri).into(binding.imageView5)
                    }

                    val emailField = document.getString("email")
                    val nameField = document.getString("name")
                    val phoneField = document.getString("phone")
                    val role = document.getString("role")

                    binding.textView24profile.text = role
                    binding.username.text = nameField.toString()
                    binding.email.text = emailField.toString()
                    binding.userPhone.text = phoneField.toString()

                } else {
                    Toast.makeText(context, "No record found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        val editProfileBtn = binding.editProfile
        val logoutBtn = binding.logoutBtn


        editProfileBtn.setOnClickListener {
            val fragmentEdit = editProfile()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.constraint_layout, fragmentEdit)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        logoutBtn.setOnClickListener {
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()

            db.collection("user").document(userEmail.toString()).get().addOnSuccessListener {topic->
                val topicLogout = topic.getString("topic")
                //unsubscribe all topics for notification
                val topic = "/topics/$topicLogout"
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            }


            val intent = Intent(activity, login_activity::class.java)
            startActivity(intent)
        }
    }

}









