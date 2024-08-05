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

class eventTableAdapter(private val dataList: List<eventTable>):
    RecyclerView.Adapter<eventTableAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventID: TextView = itemView.findViewById(R.id.evKeyHistory)
        val eventName: TextView = itemView.findViewById(R.id.donorHistory)
        val collectedAmount: TextView = itemView.findViewById(R.id.collectedAmountOrganizer)
        val targetAmount:TextView = itemView.findViewById(R.id.targetAmountOrganizer)
        val viewOrganizer: Button = itemView.findViewById(R.id.viewEventBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.eventtable, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.eventID.text = item.EVENT_KEY
        holder.eventName.text = item.evName

        holder.collectedAmount.text = item.evCollectedAmount.toString()
        holder.targetAmount.text = item.evTargetAmount.toString()

        holder.viewOrganizer.setOnClickListener{
            val context = holder.itemView.context

            val bundleEvent = Bundle()
            //bundleEvent.putString("eventName", item.evName)
            bundleEvent.putString("eventKey", item.EVENT_KEY)
            //bundleEvent.putString("evAmount", item.evCollectedAmount.toString())
            //bundleEvent.putString("event", item.evTargetAmount.toString())

            var evName = item.evName
            var evKey = item.EVENT_KEY
            var evCollect = item.evCollectedAmount.toString()
            var evTarget = item.evTargetAmount.toString()




            val fragment = eventCollectionDetail()
            fragment.arguments = bundleEvent

            val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.constraint_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}
