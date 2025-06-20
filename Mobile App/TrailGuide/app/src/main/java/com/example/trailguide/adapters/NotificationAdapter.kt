package com.example.trailguide.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trailguide.R
import com.example.trailguide.models.Notification

class NotificationAdapter(
    private val notificationList: MutableList<Notification> // Use MutableList
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animalTypeTextView: TextView = itemView.findViewById(R.id.animalTypeTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        Log.d("NotificationAdapter", "Binding notification: $notification")
        try {
            holder.animalTypeTextView.text =
                holder.itemView.context.getString(R.string.animal_label, notification.animalType)

            holder.locationTextView.text = holder.itemView.context.getString(
                R.string.location_label,
                notification.latitude,
                notification.longitude
            )

            holder.timestampTextView.text =
                holder.itemView.context.getString(R.string.time_label, notification.timestamp)
        } catch (e: Exception) {
            Log.e("NotificationAdapter", "Error binding data at position $position", e)
        }
        Log.d("NotificationAdapter", "Received notification: $notification")
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}
