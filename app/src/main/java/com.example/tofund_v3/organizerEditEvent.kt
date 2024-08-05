package com.example.tofund_v3

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.tofund_v3.databinding.FragmentOrganizerEditEventBinding
import com.google.common.primitives.UnsignedBytes.toInt
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class organizerEditEvent : Fragment() {
    private lateinit var binding: FragmentOrganizerEditEventBinding
    private var db:FirebaseFirestore = FirebaseFirestore.getInstance()
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentOrganizerEditEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val PICK_IMAGE_REQUEST = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //loading box
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        //Receive bundle argument
        var eventKey = arguments?.getString("eventKey")
        Log.d("eventname", "receive bundle $eventKey")
        binding.edittxtEventName.setText(eventKey)

        //get image
        val imageBtn = binding.eventImage
        imageBtn.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Upload Profile Photo")
                .setMessage("You want to get photo from gallery?").setCancelable(true)
                .setPositiveButton("Yes") { dialog2, which ->
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
                }.show()
        }

        binding.imageButton3.setOnClickListener{
            val fragmentBack = organizerEvent()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.constraint_layout, fragmentBack)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        db.collection("event").document(eventKey.toString()).get()
            .addOnSuccessListener { condition ->
                val evCondition = condition.getString("condition").toString()

                if (evCondition == "banned") {
                    val alertDialog = AlertDialog.Builder(context)
                        .setTitle("Event Banned")
                        .setMessage("This event $eventKey has been banned by Admin\n Contact Admin For Further Information\n Admin Phone : 017-5942833")
                        .setPositiveButton("OK") { dialog, _ ->

                            progressDialog.dismiss()

                            val editEventDone = organizerEvent()
                            val fragmentManager =
                                requireActivity().supportFragmentManager
                            val transaction = fragmentManager.beginTransaction()
                            transaction.replace(R.id.constraint_layout, editEventDone)
                            transaction.addToBackStack(null)
                            transaction.commit()


                        }
                        .setCancelable(false)
                        .create()

                    alertDialog.show()
                } else {



                    //retrieve event image from fire store
                    val imageRef =
                        FirebaseStorage.getInstance()
                            .getReference("eventPicture/${eventKey}EventPic.jpg")
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val imageUri = downloadUri.toString()

                        Glide.with(this).load(imageUri).into(binding.eventImage)
                    }

                    val eventNameFirestore = eventKey.toString()
                    val docRef = db.collection("event").document(eventNameFirestore)

                    docRef.get().addOnSuccessListener { documentSnapshot ->
                        val eventKey = documentSnapshot.get("EVENT_KEY")
                        Log.d("try11", "Error getting document: $eventKey")
                    }.addOnFailureListener { exception ->
                        Log.d("try", "Error getting document: ", exception)
                    }






                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                progressDialog.dismiss()

                                val descField = document.getString("evDescription")
                                val ecNameField = document.getString("evName")
                                val amountField = document.getLong("evTargetAmount")
                                val eventkeyID = document.getString("EVENT_KEY")
                                val collectAmount = document.getLong("evCollectedAmount")
                                val topic = document.getString("topic")

                                var amount = amountField.toString()

                                binding.edittxtDesc.setText(descField)
                                binding.edittxtEventName.setText(ecNameField)
                                binding.edittxtEventID.setText(eventkeyID)


                                if (collectAmount != null) {
                                    binding.collectamount.setText(collectAmount.toString())
                                }
                                if (amountField != null) {
                                    binding.edittxtTarget.setText(amount)
                                }

                            } else {
                                Toast.makeText(context, "No record found", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(ContentValues.TAG, "get failed with ", exception)
                        }


                    val editEventDone = binding.editBtn
                    val delEventBtn = binding.delBtn

                    //delete Event
                    delEventBtn.setOnClickListener {
                        //Alert box for delete event
                        val builder = AlertDialog.Builder(context)

                        builder.setTitle("Are you sure to DELETE Event?")
                            .setMessage("Event can not recover once deleted!").setCancelable(true)
                            .setPositiveButton("Yes") { dialog, which ->
                                db.collection("event").document(eventNameFirestore).delete()
                                Toast.makeText(
                                    context,
                                    "Event with KEY ($eventNameFirestore) is Deleted",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val editEventDone = organizerEvent()
                                val fragmentManager = requireActivity().supportFragmentManager
                                val transaction = fragmentManager.beginTransaction()
                                transaction.replace(R.id.constraint_layout, editEventDone)
                                transaction.addToBackStack(null)
                                transaction.commit()

                            }.setNegativeButton("No") { dialog, which ->
                                dialog.cancel()
                            }.show()
                    }

                    //edit Event
                    editEventDone.setOnClickListener {
                        try {
                            db.collection("event").document(eventKey.toString()).get()
                                .addOnSuccessListener { topic ->
                                    val topic = topic.getString("topic")


                                    //retreive session
                                    val sharedPref =
                                        requireContext().getSharedPreferences(
                                            "loginSession",
                                            Context.MODE_PRIVATE
                                        )
                                    val userEmail = sharedPref.getString("userEmail", "").toString()
                                    Log.d("sessionValueTest2", "login email = $userEmail")

                                    val newEvName = binding.edittxtEventName.text.toString()
                                    val newTarget = binding.edittxtTarget.text.toString().toInt()
                                    val newDesc = binding.edittxtDesc.text.toString()
                                    val newCollected = binding.collectamount.text.toString().toInt()

                                    var event_KEY_Uri = eventKey

                                    // Upload the selected image to Firebase Storage
                                    if (imageUri != null) {
                                        if (event_KEY_Uri != null) {
                                            uploadImageToFirebaseStorage(event_KEY_Uri.toUri(), imageUri!!.toString())
                                        }
                                    }


                                    //record new data
                                    val editData = hashMapOf(
                                        "EVENT_KEY" to eventKey,
                                        "evDescription" to newDesc,
                                        "evName" to newEvName,
                                        "createdBy" to userEmail,
                                        "evTargetAmount" to newTarget,
                                        "evCollectedAmount" to newCollected,
                                        "topic" to topic,
                                        "condition" to "active"
                                    )

                                    //update data to Fire store
                                    if (newDesc.isNotEmpty() && newEvName.isNotEmpty()) {

                                        db.collection("event").document(eventNameFirestore)
                                            .set(editData)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Event Edited Successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }


                                        db.collection("donation").document(eventNameFirestore).get()
                                            .addOnSuccessListener { document ->
                                                val oldCollect =
                                                    document.getLong("evCollectedAmount").toString()
                                                        .toInt()
                                                val oldTargetDonate =
                                                    document.getLong("evStillNeedAmount").toString()
                                                        .toInt()
                                                val oldNeed =
                                                    document.getLong("evTargetAmount").toString()
                                                        .toInt()

                                                val newCollect = oldCollect
                                                val newTargetDonate = newTarget
                                                val newStillNeed = newTargetDonate - newCollect

                                                if (newTarget > newCollect) {
                                                    val updateDonate = hashMapOf(
                                                        "EVENT_KEY" to eventKey,
                                                        "evCollectedAmount" to newCollect,
                                                        "evTargetAmount" to newTargetDonate,
                                                        "evStillNeedAmount" to newStillNeed
                                                    )

                                                    db.collection("donation")
                                                        .document(eventNameFirestore)
                                                        .set(updateDonate)
                                                        .addOnSuccessListener {
                                                            Toast.makeText(
                                                                context,
                                                                "Event Edited Successfully",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "New Target AMount RM$newTargetDonate is less than or equal to RM ($newCollect), So Event is Completed",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }


                                            }

                                        val editEventDone = organizerEvent()
                                        val fragmentManager =
                                            requireActivity().supportFragmentManager
                                        val transaction = fragmentManager.beginTransaction()
                                        transaction.replace(R.id.constraint_layout, editEventDone)
                                        transaction.addToBackStack(null)
                                        transaction.commit()

                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Please Fill Up all Fields",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } catch (e: NumberFormatException) {
                            Toast.makeText(
                                context,
                                "Please enter a valid number for target amount",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            this.imageUri = data.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, this.imageUri)
            binding.eventImage.setImageBitmap(imageBitmap)
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