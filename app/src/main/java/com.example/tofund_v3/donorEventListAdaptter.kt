package com.example.tofund_v3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class donorEventListAdaptter(private val donorEventList: ArrayList<Event>):
    RecyclerView.Adapter<donorEventListAdaptter.ViewHolderDonor>(){
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var onClickListenerDonor: OnClickListenerDonor? = null

    fun setOnClickListenerDonor(onClickListenerDonor: OnClickListenerDonor) {
        this.onClickListenerDonor = onClickListenerDonor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): donorEventListAdaptter.ViewHolderDonor {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.eventlist, parent, false)
        return ViewHolderDonor(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderDonor, position: Int) {
        val event: Event = donorEventList[position]
        holder.evName.text = event.evName
        holder.evKey.text = event.EVENT_KEY
        val eventName = event.evName.toString()
        val eventKey = event.EVENT_KEY.toString()
        holder.evName.setOnClickListener {
            onClickListenerDonor?.onClick(position, event, eventName, eventKey)

            val eventName = holder.evName.text.toString()
        }
    }


    override fun getItemCount(): Int {
        return donorEventList.size
    }

    inner class ViewHolderDonor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val evName: TextView = itemView.findViewById(R.id.eventlistName)
        val evKey: TextView = itemView.findViewById(R.id.key)
    }

    interface OnClickListenerDonor {
        fun onClick(position: Int, event: Event, eventName: String, eventKey:String)
    }
}