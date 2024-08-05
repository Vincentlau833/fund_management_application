package com.example.tofund_v3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class historyAdapter(private val historyList : List<donationHistory>):
    RecyclerView.Adapter<historyAdapter.historyViewHolder>() {


    inner class historyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventID: TextView = itemView.findViewById(R.id.evKeyHistory)
        val viewBtn: Button = itemView.findViewById(R.id.ViewHistoryBtn)
        val eventName: TextView = itemView.findViewById(R.id.eventNameHistory)
        val donorName: TextView = itemView.findViewById(R.id.donorNameInvisible)
        val totalAmount: TextView = itemView.findViewById(R.id.amountHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): historyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.historytable, parent, false)

        return historyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: historyViewHolder, position: Int) {
        val item = historyList[position]
        holder.eventID.text = item.EVENT_KEY
        holder.donorName.text = item.donorName
        holder.totalAmount.text = item.donateAmount
        holder.eventName.text = item.eventName

        holder.viewBtn.setOnClickListener{
        val context = holder.itemView.context

        val bundleHistory = Bundle()
        bundleHistory.putString("donorName", item.donorName)
        bundleHistory.putString("eventName", item.eventName)
        bundleHistory.putString("eventKey", item.EVENT_KEY)

        Log.d("eventNameBundle", "${item.eventName}")

        val historyfragment = donorHistoryDetail()
        historyfragment.arguments = bundleHistory

        val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.constraint_layout, historyfragment)
        transaction.addToBackStack(null)
        transaction.commit()
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}
