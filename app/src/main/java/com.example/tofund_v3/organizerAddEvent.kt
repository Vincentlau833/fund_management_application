package com.example.tofund_v3

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.tofund_v3.databinding.FragmentOrganizerAddEventBinding
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlin.random.Random

class organizerAddEvent : Fragment() {
    private lateinit var binding:FragmentOrganizerAddEventBinding
    private val db:FirebaseFirestore = FirebaseFirestore.getInstance()
    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrganizerAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val PICK_IMAGE_REQUEST = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addEventBtn = binding.addNewEventBtn



        binding.backButtonAddEvent.setOnClickListener{
            val fragmentBack = organizerEvent()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.constraint_layout, fragmentBack)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        //onclick call alert box
        binding.imageBtn.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Upload Profile Photo")
                .setMessage("You want to get photo from gallery?").setCancelable(true)
                .setPositiveButton("Yes") { dialog2, which ->
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
                }.show()
        }

        addEventBtn.setOnClickListener{
            addEvent()
        }
    }


    private fun addEvent(){

        //retreive session
        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val createdBy = sharedPref.getString("userEmail", "").toString()
        Log.d("sessionValueTest2","login email = $createdBy")





        var event_KEY_Uri = binding.eventKey.text.toString()

        // Upload the selected image to Firebase Storage
        if (imageUri != null) {
            uploadImageToFirebaseStorage(event_KEY_Uri.toUri(), imageUri!!.toString())
        }


        try {
            val event_KEY = binding.eventKey.text.toString().uppercase()
            val eventName = binding.newEventName.text.toString()
            val targetAmount = binding.newTargetAmount.text.toString().toInt()
            val desc = binding.newEventDesc.text.toString()
            val docRef = db.collection("event").document(eventName)

            var keyValidation = db.collection("event").whereEqualTo(FieldPath.documentId(), event_KEY)
            var deletedKeyValidation = db.collection("deletedEvent").whereEqualTo(FieldPath.documentId(), event_KEY)

            deletedKeyValidation.get().addOnCompleteListener { delTask ->
                if (delTask.isSuccessful) {
                    val delQuery = delTask.result
                    if (delQuery != null && !delQuery.isEmpty) {
                        Toast.makeText(
                            context,
                            "Event Key already registered by other Organizer",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        keyValidation.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val querySnapshot = task.result
                                if (querySnapshot != null && !querySnapshot.isEmpty) {
                                    Toast.makeText(
                                        context,
                                        "Event Key already registered by other Organizer",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val evCollectedAmount = 0

                                    db.collection("user").document(createdBy).get().addOnSuccessListener { topic ->
                                        val topicName = topic.getString("topic")

                                        //record new event data
                                        val newEvent = hashMapOf(
                                            "evName" to eventName,
                                            "evTargetAmount" to targetAmount,
                                            "evDescription" to desc,
                                            "createdBy" to createdBy,
                                            "EVENT_KEY" to event_KEY,
                                            "evCollectedAmount" to evCollectedAmount,
                                            "topic" to topicName,
                                            "condition" to "active"
                                        )

                                        val collectedAmount = 0
                                        val newEventDonationData = hashMapOf(
                                            "evTargetAmount" to targetAmount,
                                            "evCollectedAmount" to collectedAmount,
                                            "evStillNeedAmount" to targetAmount,
                                            "EVENT_KEY" to event_KEY
                                        )

                                        if (event_KEY.isNotEmpty() && eventName.isNotEmpty() && desc.isNotEmpty() && targetAmount > 499) {
                                            db.collection("donation").document(event_KEY).set(newEventDonationData)
                                            db.collection("event").document(event_KEY).set(newEvent)

                                            val fragmentAddEvent = organizerEvent()
                                            val fragmentManager = requireActivity().supportFragmentManager
                                            val transaction = fragmentManager.beginTransaction()
                                            transaction.replace(R.id.constraint_layout, fragmentAddEvent)
                                            transaction.addToBackStack(null)
                                            transaction.commit()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Fail to add Event (Targeted Amount must be more than 500)",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Please enter a valid number for target amount", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            this.imageUri = data.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, this.imageUri)
            binding.logoToFund.setImageBitmap(imageBitmap)
            //this.imageUri = saveImageToStorage(imageBitmap)
        }
    }


    private fun uploadImageToFirebaseStorage(imageUri: Uri?, email: String) {
        var finalUri = email.toUri()
        var finalpathname = imageUri.toString()


        val storageRef = FirebaseStorage.getInstance().reference.child("eventPicture/${finalpathname.uppercase()}EventPic.jpg")

        storageRef.putFile(finalUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val imagePath = storageRef.path
                val imageData = hashMapOf(
                    "profilePath" to imagePath
                )

                val documentId = email.replace("/", "-")

                db.collection("user").document(documentId)
                    .update(imageData as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Image path successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error updating image path: $e")
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("uploadImageToFirebase", "Error uploading image: ${exception.message}")
            }
    }


}