package com.example.tofund_v3

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tofund_v3.databinding.FragmentSelectDonationDetailsBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class selectDonationDetails : Fragment() {
    private lateinit var binding: FragmentSelectDonationDetailsBinding
    private var db:FirebaseFirestore = FirebaseFirestore.getInstance()

    val TAG = "donateNotification"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectDonationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        //get session value
        val sharedPref = requireContext().getSharedPreferences("loginSession", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "").toString()

        //Receive bundle argument
        var eventName = arguments?.getString("eventName").toString()
        var eventKey = arguments?.getString("key").toString()
        Log.d("donationDetails","event key = $eventKey")
        //Log.d("evName","event key = $eventName")







        db.collection("event").document(eventKey).get().addOnSuccessListener { document->
            binding.eventID.text = document.getString("EVENT_KEY")
        }

        db.collection("donation").document(eventKey).get().addOnSuccessListener {document->
            binding.amountTxtView.text = document.getLong("evStillNeedAmount").toString()
        }

        //val donorName = binding.donorName.text.toString()
        var donateAmount = binding.donationAmount

        val donate10 = binding.amount10
        val donate20 = binding.amount20
        val donate30 = binding.amount30
        val donate50 = binding.amount50
        val donate100 = binding.amount100
        val donate200 = binding.amount200

        //set button background tint color programmatically
        donate10.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tofundGreen))
        donate20.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tofundGreen))
        donate30.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tofundGreen))
        donate50.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tofundGreen))
        donate100.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tofundGreen))
        donate200.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tofundGreen))

        donate10.setOnClickListener{
            donateAmount.setText("10")
        }
        donate20.setOnClickListener{
            donateAmount.setText("20")
        }
        donate30.setOnClickListener{
            donateAmount.setText("30")
        }
        donate50.setOnClickListener{
            donateAmount.setText("50")
        }
        donate100.setOnClickListener{
            donateAmount.setText("100")
        }
        donate200.setOnClickListener{
            donateAmount.setText("200")
        }

        var confirmDonateBtn = binding.confirmDonateBtn

        confirmDonateBtn.setOnClickListener{
            db.collection("donation").document(eventKey).get().addOnSuccessListener { document ->
                val eventName = document.getString("eventName")
                val eventKey = document.getString("EVENT_KEY")
                val collectedAmount = document.getLong("evCollectedAmount")
                val neededAmount = document.getLong("evStillNeedAmount")
                val targetAmount = document.getLong("evTargetAmount")


                val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val donateDateTime = LocalDateTime.now().format(dateTimeFormat)

                //log amount for easier debug
                Log.d("key", "$eventKey")
                Log.d("amount1", "$collectedAmount")
                Log.d("amount2", "$neededAmount")
                Log.d("amount3", "$targetAmount")



                val userDonatedAmountString = binding.donationAmount.text.toString()

                //set default value for empty string
                val userDonatedAmount = if (TextUtils.isEmpty(userDonatedAmountString)) {
                    0
                } else {
                    userDonatedAmountString.toInt()
                }

                var donorName = binding.donorName.text.toString()

                if(userDonatedAmount < 10 || donorName.isEmpty()){
                    Toast.makeText(context,"Please Fill up Donor name and Donation Amount(Minimum RM 10)",Toast.LENGTH_SHORT).show()
                }else {
                    if (userDonatedAmount > neededAmount.toString().toInt()) {
                        Toast.makeText(context, "Amount Exceeded", Toast.LENGTH_SHORT).show()
                    } else {
                        db.collection("event").document(eventKey.toString()).get()
                            .addOnSuccessListener { topic ->
                                val userTopic = topic.getString("topic")
                                val topic = "/topics/$userTopic"



                                //Notification//
                                val title = "Receive Donation"
                                val message =
                                    "$donorName donated RM ${donateAmount.text} to event ${eventKey}"

                                if (title.isNotEmpty() && message.isNotEmpty()) {
                                    PushNotification(
                                        NotificationData(title, message),
                                        topic
                                    ).also {
                                        sendNotification(it)
                                    }

                                }



                                var newCollectedAmount = collectedAmount?.plus(userDonatedAmount)
                                Log.d("calculatedAmount", "$newCollectedAmount")

                                var newNeededAmount = neededAmount?.minus(userDonatedAmount)
                                Log.d("newNeededAmount", "$newNeededAmount")


                                var newDonationData = hashMapOf(
                                    "eventName" to eventName,
                                    "evTargetAmount" to targetAmount,
                                    "evCollectedAmount" to newCollectedAmount,
                                    "evStillNeedAmount" to newNeededAmount,
                                    "EVENT_KEY" to eventKey
                                )

                                db.collection("donation").document(eventKey.toString())
                                    .set(newDonationData)



                                var currentDonation: Long = 0

                                val evKey = hashMapOf(
                                    "EVENT_KEY" to eventKey
                                )
                                db.collection("donorList").document(eventKey.toString()).set(evKey)




                                db.collection("donorList").document(eventKey.toString())
                                    .collection(userEmail)
                                    .document(donorName).get().addOnSuccessListener { document ->
                                        val evKey = hashMapOf(
                                            "EVENT_KEY" to eventKey
                                        )
                                        db.collection("donorList").document(eventKey.toString())
                                            .set(evKey)


                                        currentDonation = document.getLong("donateAmount") ?: 0
                                        Log.d("currentDonation", "$currentDonation")

                                        var totalDonation = currentDonation + userDonatedAmount
                                        Log.d(
                                            "calculation",
                                            "$totalDonation = $currentDonation + $userDonatedAmount"
                                        )

                                        var eventName = arguments?.getString("eventName").toString()

                                        //add donor detail to Firestore
                                        val donorDetailData = hashMapOf(
                                            "EVENT_KEY" to eventKey,
                                            "eventName" to eventName,
                                            "donorName" to donorName,
                                            "donateAmount" to totalDonation,
                                            "donateDateTime" to donateDateTime
                                        )


                                        val newDonor = hashMapOf(
                                            "EVENT_KEY" to eventKey,
                                            "donateAmount" to totalDonation,
                                            "donorName" to donorName
                                        )



                                        db.collection("donor").document("donor Name List")
                                            .collection(eventKey.toString()).document(donorName)
                                            .set(newDonor)



                                        db.collection("donorList").document(eventKey.toString())
                                            .collection(userEmail)
                                            .document(donorName).set(donorDetailData)

                                        db.collection("event").document(eventKey.toString()).get()
                                            .addOnSuccessListener { document ->
                                                var createdBy = document.getString("createdBy")
                                                var evDescription =
                                                    document.getString("evDescription")

                                                var evName = document.getString("evName")
                                                var evTargetAmount =
                                                    document.getLong("evTargetAmount")?.toInt() ?: 0

                                                val updateEventdata = hashMapOf(
                                                    "EVENT_KEY" to eventKey,
                                                    "createdBy" to createdBy,
                                                    "evDescription" to evDescription,
                                                    "evName" to evName,
                                                    "evTargetAmount" to evTargetAmount,
                                                    "evCollectedAmount" to newCollectedAmount,
                                                    "condition" to "active",
                                                    "topic" to userTopic
                                                )
                                                db.collection("event").document(eventKey.toString())
                                                    .set(updateEventdata)
                                            }
                                    }
                                Toast.makeText(
                                    context,
                                    "You have Donated RM $userDonatedAmount to $eventKey",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(activity, donationSuccess::class.java)
                                startActivity(intent)
                            }
                    }
                }
            }
        }

        binding.imageButton2.setOnClickListener{
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
    }


    //Notification function Part
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try{
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.d(TAG,"Response : ${Gson().toJson(response)}")
            }else{
                Log.e(TAG, response.errorBody().toString())
            }
        }catch (e: java.lang.Exception){
            Log.e(TAG, e.toString())
        }
    }
}