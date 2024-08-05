package com.example.tofund_v3

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

private var db:FirebaseFirestore = FirebaseFirestore.getInstance()

class adminEventAdapter(private var dataList: List<eventTable>):
    RecyclerView.Adapter<adminEventAdapter.ViewHolderEvent>() {



    private var filterList = dataList

    inner class ViewHolderEvent(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventID: TextView = itemView.findViewById(R.id.adminEventKey)
        val eventName: TextView = itemView.findViewById(R.id.adminEventName)
        val banBtn: Button = itemView.findViewById(R.id.adminEventBtn)
        val activeBtn: Button = itemView.findViewById(R.id.recoverBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): adminEventAdapter.ViewHolderEvent {
        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.admin_event_table, parent, false)

        return ViewHolderEvent(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    override fun onBindViewHolder(holder: ViewHolderEvent, position: Int) {
        val item = dataList[position]
        holder.eventID.text = item.EVENT_KEY
        holder.eventName.text = item.evName

        // Enable/disable buttons based on condition
        holder.banBtn.isEnabled = item.condition != "banned"
        holder.activeBtn.isEnabled = item.condition != "active"

        holder.activeBtn.setOnClickListener {
            // Show confirmation dialog
            val context = holder.itemView.context
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setMessage("Are you sure to Recover this Event?\n" +
                    "EVENTKEY : ${holder.eventID.text}\n" +
                    "Event Name : ${holder.eventName.text}")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    // Update Firestore document
                    val condition = hashMapOf("condition" to "active")
                    db.collection("event").document(item.EVENT_KEY.toString()).update(condition as Map<String, Any>)
                        .addOnSuccessListener {
                            // Update the local data and notify the adapter
                            item.condition = "active"
                            notifyItemChanged(holder.adapterPosition)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Something when wrong", Toast.LENGTH_SHORT).show()
                        }
                    dialog.dismiss()
                }.setNegativeButton("No"){dialog, no->
                    dialog.dismiss()
                }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        holder.banBtn.setOnClickListener {
            // Show confirmation dialog
            val context = holder.itemView.context
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setMessage("Are you sure to Ban this Event?\n" +
                    "EVENTKEY : ${holder.eventID.text}\n" +
                    "Event Name : ${holder.eventName.text}")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    // Update Firestore document
                    val condition = hashMapOf("condition" to "banned")
                    db.collection("event").document(item.EVENT_KEY.toString()).update(condition as Map<String, Any>)
                        .addOnSuccessListener {
                            // Update the local data and notify the adapter
                            item.condition = "banned"
                            notifyItemChanged(holder.adapterPosition)
                        }
                        .addOnFailureListener { exception ->

                        }
                    dialog.dismiss()
                }.setNegativeButton("No"){dialog, no->
                    dialog.dismiss()
                }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }



}