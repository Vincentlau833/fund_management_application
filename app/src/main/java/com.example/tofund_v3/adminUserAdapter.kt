package com.example.tofund_v3

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

class adminUserAdapter(private var dataList: List<AdminUser>):
    RecyclerView.Adapter<adminUserAdapter.ViewHolderUser>() {

    inner class ViewHolderUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userEmail: TextView = itemView.findViewById(R.id.adminUserKey)
        val role: TextView = itemView.findViewById(R.id.roleAdmin)
        val username: TextView = itemView.findViewById(R.id.usernameAdmin)
        val banBtn: Button = itemView.findViewById(R.id.adminUserBtn)
        val activeBtn: Button = itemView.findViewById(R.id.recoverUserBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): adminUserAdapter.ViewHolderUser {
        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.admin_user_table, parent, false)

        return ViewHolderUser(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderUser, position: Int) {
        val item = dataList[position]
        holder.userEmail.text = item.email
        holder.role.text = item.role
        holder.username.text = item.username

        // Enable/disable buttons based on condition
        holder.banBtn.isEnabled = item.status != "banned"
        holder.activeBtn.isEnabled = item.status != "active"

        holder.activeBtn.setOnClickListener {
            // Show confirmation dialog
            val context = holder.itemView.context
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setMessage("Are you sure to Activate this User\n?" +
                    "Email : ${holder.userEmail.text}\n" +
                    "Name : ${holder.username.text}")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    // Update Firestore document
                    val condition = hashMapOf("status" to "active")
                    db.collection("user").document(item.email.toString()).update(condition as Map<String, Any>)
                        .addOnSuccessListener {
                            // Update the local data and notify the adapter
                            item.status = "active"
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
            alertDialogBuilder.setMessage("Are you sure to Ban this User?\n" +
                    "Email : ${holder.userEmail.text}\n" +
                    "Name : ${holder.username.text}")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    // Update Firestore document
                    val condition = hashMapOf("status" to "banned")
                    db.collection("user").document(item.email.toString()).update(condition as Map<String, Any>)
                        .addOnSuccessListener {
                            // Update the local data and notify the adapter
                            item.status = "banned"
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