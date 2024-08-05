package com.example.tofund_v3


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

//add context:Context for session
class eventListAdapter(private val eventList: ArrayList<Event>) :
    RecyclerView.Adapter<eventListAdapter.MyViewHolder>() {
    private var db:FirebaseFirestore = FirebaseFirestore.getInstance()

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.eventlist, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val event: Event = eventList[position]
        holder.evName.text = event.EVENT_KEY
        val eventName = event.EVENT_KEY.toString()
        holder.evName.setOnClickListener {
            onClickListener?.onClick(position, event, eventName)

            val eventName = holder.evName.text.toString()
        }

    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val evName: TextView = itemView.findViewById(R.id.eventlistName)
    }

    interface OnClickListener {
        fun onClick(position: Int, event: Event, eventKey: String)
    }
}
