package com.example.tofund_v3

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tofund_v3.databinding.FragmentEventCollectionDetailBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.firestore.FirebaseFirestore
//import kotlin.collections.EmptyMap.entries


class eventCollectionDetail : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var binding: FragmentEventCollectionDetailBinding
    private var db:FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventCollectionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    //loading box
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val evKey = arguments?.getString("eventKey").toString()

        binding.backButton.setOnClickListener{
            val fragmentBack = organizerEventList()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.constraint_layout, fragmentBack)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        db.collection("event").document(evKey).get().addOnSuccessListener {document->
            progressDialog.dismiss()

            var eventKEY = document.getString("EVENT_KEY").toString()
            var eventName = document.getString("evName").toString()
            var eventCollectedAmount = document.getLong("evCollectedAmount").toString()
            var eventTargetAmount = document.getLong("evTargetAmount").toString()

            binding.eventKeyDetail.text = eventKEY
            binding.eventDetailName.text = eventName
            binding.targetDetail.text = eventTargetAmount

            val toIntCollect = eventCollectedAmount.toInt()
            val toIntTarget = eventTargetAmount.toInt()
            binding.collectedDetail.text = eventCollectedAmount

            if(toIntCollect == null){
                binding.collectedDetail.text = "0"
            }else {
                val progress = (toIntCollect.toDouble() / toIntTarget) * 100
                val chartProgress = String.format("%.2f", progress)

                //pie chart
                pieChart = binding.pieChart

                pieChart.setUsePercentValues(true)
                pieChart.description.isEnabled = false
                pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

                pieChart.dragDecelerationFrictionCoef = 0.95f

                pieChart.isDrawHoleEnabled = true
                pieChart.setHoleColor(Color.WHITE)

                pieChart.setTransparentCircleColor(Color.WHITE)
                pieChart.setTransparentCircleAlpha(110)

                pieChart.holeRadius = 58f
                pieChart.transparentCircleRadius = 61f

                pieChart.setDrawCenterText(true)

                pieChart.rotationAngle = 0f

                pieChart.isRotationEnabled = true
                pieChart.isHighlightPerTapEnabled = true

                pieChart.animateY(1000, Easing.EaseInOutQuad)

                pieChart.legend.isEnabled = false
                pieChart.setEntryLabelColor(Color.BLACK)
                pieChart.setEntryLabelTextSize(10f)

                val entries: ArrayList<PieEntry> = ArrayList()
                val originalPercent = 100.00f
                val remainPercent = originalPercent - chartProgress.toFloat()
                //add %


                //set Value to the pie chart

                if(progress.toFloat() != 0.00f){
                    entries.add(PieEntry(progress.toFloat(), "Collected Amount"))
                }

                if(remainPercent.toFloat() != 0.00f){
                    entries.add(PieEntry(remainPercent, "Still Need Amount"))
                }
                val dataSet = PieDataSet(entries,"Donation Progress")

                dataSet.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return String.format("%.2f%%", value)
                    }
                }


                dataSet.setDrawIcons(false)
                dataSet.sliceSpace = 3f
                dataSet.iconsOffset = MPPointF(0f, 40f)
                dataSet.selectionShift = 5f

                // add Color
                val colors: ArrayList<Int> = ArrayList()
                colors.add(resources.getColor(R.color.purple_200))
                colors.add(resources.getColor(R.color.tofundGreen))

                dataSet.colors = colors

                val data = PieData(dataSet)
                //data.setValueFormatter(PercentFormatter())
                data.setValueTextSize(15f)
                data.setValueTypeface(Typeface.DEFAULT_BOLD)
                data.setValueTextColor(Color.WHITE)
                pieChart.data = data

                pieChart.highlightValues(null)
                pieChart.invalidate()
            }
        }



        //recycler view for donor list
        val donorList = mutableListOf<donorList>()

        val donorRef = db.collection("donor").document("donor Name List")

        donorRef.collection(evKey).get().addOnSuccessListener {document->
            for(document in document.documents){
                val donorName = document.getString("donorName")
                val donateAmount = document.getLong("donateAmount").toString()

                val donorData = donorList(
                    donateName = donorName,
                    donateAmount = donateAmount
                )
                donorList.add(donorData)
            }
            // Display the data in the table layout
            if (donorList.isNotEmpty()) {
                displayData(donorList)
            }
        }
    }

    private fun displayData(donorList: List<donorList>) {
        // Initialize the recycler view and adapter
        binding.donorListRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.donorListRecyclerView.adapter= donorListAdapter(donorList)
    }

}