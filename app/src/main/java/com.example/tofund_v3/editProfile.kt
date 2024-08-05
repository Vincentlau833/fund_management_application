package com.example.tofund_v3
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import androidx.core.net.toUri
import com.example.tofund_v3.databinding.FragmentEditProfileBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlin.random.Random
import com.bumptech.glide.Glide


class editProfile : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private val db:FirebaseFirestore = FirebaseFirestore.getInstance()
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { FirebaseApp.initializeApp(it) }

    }

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val PICK_IMAGE_REQUEST = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //loading box
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val saveBtn = binding.save
        val cancelBtn = binding.cancelBtn

        //retreive session
        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val email = sharedPref.getString("userEmail", "").toString()
        Log.d("sessionValueTest2","login email = $email")

        //Display Profile Image from firebase storage
        val storageRef = FirebaseStorage.getInstance().reference.child("profilePicture/${email}ProfilePic.jpg")
        val imageRef = FirebaseStorage.getInstance().getReference("profilePicture/${email}ProfilePic.jpg")
        imageRef.downloadUrl.addOnSuccessListener { downloadUri->
            val imageUri = downloadUri.toString()

            Glide.with(this).load(imageUri).into(binding.imageView5)
        }

        val docRef = db.collection("user").document(email)
        //display original data
        docRef.get().addOnSuccessListener { document ->
            progressDialog.dismiss()

            if (document != null) {
                val emailField = document.getString("email")
                val nameField = document.getString("name")
                val phoneField = document.getString("phone")

                binding.username.setText(nameField)
                binding.email.setText(emailField)
                binding.userPhone.setText(phoneField)

            } else {
                Toast.makeText(context, "No record found", Toast.LENGTH_SHORT).show()
            }
        }

        //profile photo
        binding.profilePic.setOnClickListener {
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Upload Profile Photo")
                .setMessage("You want to get from gallery or take a photo now?").setCancelable(true)
                .setPositiveButton("Camera") { dialog1, which ->

                    // Check for camera permission
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // Request the CAMERA permission
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
                       } else {
                        //launch camera once the permission is granted
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                    }

                }.setNegativeButton("Gallery") { dialog2, which ->
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
                }.show()
        }

        saveBtn.setOnClickListener {
            //loading box
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Loading...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            docRef.get().addOnSuccessListener { document ->
                val topic = document.getString("topic")
                val role = document.getString("role")

                //Get New Data
                val newName = binding.username.text.toString()
                val newPhone = binding.userPhone.text.toString()

                //record new data
                val editData = hashMapOf(
                    "email" to email,
                    "name" to newName,
                    "phone" to newPhone,
                    "role" to role,
                    "topic" to topic,
                    "status" to "active"
                )

                // Upload the selected image to Firebase Storage
                if (imageUri != null) {
                    uploadImageToFirebaseStorage(email.toUri(), imageUri!!.toString())
                }

                //update data to Fire store
                if (email.isNotEmpty() && newName.isNotEmpty() &&newName.length > 5 && newPhone.isNotEmpty()) {
                    if(isValidMalaysiaPhoneNumber(newPhone)) {
                        db.collection("user").document(email).set(editData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Profile Edited Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    }else{
                        Toast.makeText(context,"Please Enter Valid Phone Number (012-3456789)",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context,"Please Check Your Input (Name should more than 5 characters)",Toast.LENGTH_SHORT).show()
                }

                val fragmentSave = profileFragment()
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.constraint_layout, fragmentSave)
                transaction.addToBackStack(null)
                transaction.commit()
                progressDialog.dismiss()
            }
        }

        cancelBtn.setOnClickListener{
            val fragmentSave = profileFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.constraint_layout, fragmentSave)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            binding.imageView5.setImageBitmap(imageBitmap)
            // Save the image to storage
            this.imageUri = saveImageToStorage(imageBitmap)

        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            this.imageUri = data.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, this.imageUri)
            binding.imageView5.setImageBitmap(imageBitmap)
            this.imageUri = saveImageToStorage(imageBitmap)
        }
    }

    private fun saveImageToStorage(bitmap: Bitmap): Uri? {
        val random = Random(System.currentTimeMillis())
        val randomNumber = random.nextInt(100000, 999999)
        val displayName = "$randomNumber.jpg"

        val resolver = requireActivity().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        var imageUri: Uri? = null
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
            imageUri = uri
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
            }
        }
        return imageUri
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 123
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri?, email: String) {
        var finalUri = email.toUri()
        var finalEmail = imageUri.toString()



        val storageRef = FirebaseStorage.getInstance().reference.child("profilePicture/${finalEmail}ProfilePic.jpg")

        storageRef.putFile(finalUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val imagePath = storageRef.path
                val imageData = hashMapOf(
                    "profilePath" to imagePath
                )

                val documentId = email.replace("/", "-") // Replace any slashes in email with hyphen (-)

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

    fun isValidMalaysiaPhoneNumber(phoneNumber: String): Boolean {
        val malaysiaPhoneRegex = "^(\\+?6?01)[0-46-9]-*[0-9]{7,8}\$"
        return phoneNumber.matches(malaysiaPhoneRegex.toRegex())
    }
}