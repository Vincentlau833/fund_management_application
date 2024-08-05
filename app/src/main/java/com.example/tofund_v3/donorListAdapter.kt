package com.example.tofund_v3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.NonDisposableHandle.parent

class donorListAdapter(private val donorList: List<donorList>):
    RecyclerView.Adapter<donorListAdapter.donorListViewHolder>() {


    inner class donorListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorNameTextView: TextView = itemView.findViewById(R.id.donorNameText)
        val donateAmountTextView: TextView = itemView.findViewById(R.id.donorAmountText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): donorListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.donorlisttable, parent, false)

        return donorListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: donorListViewHolder, position: Int) {
        val donor = donorList[position]
        holder.donorNameTextView.text = donor.donateName
        holder.donateAmountTextView.text = donor.donateAmount.toString()

    }

    override fun getItemCount(): Int {
        return donorList.size
    }
}