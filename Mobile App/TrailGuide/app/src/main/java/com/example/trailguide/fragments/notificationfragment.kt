package com.example.trailguide.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trailguide.R
import com.example.trailguide.adapters.NotificationAdapter
import com.example.trailguide.models.Notification
import com.example.trailguide.utils.BluetoothManager
import com.example.trailguide.viewmodels.SharedViewModel
import kotlinx.coroutines.*

class notificationfragment : Fragment() {

    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private val notificationList = mutableListOf<Notification>()
    private var updateJob: Job? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() // Use a shared ViewModel to communicate with MapFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_nortification, container, false)

        notificationRecyclerView = view.findViewById(R.id.notificationRecyclerView)
        notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        notificationAdapter = NotificationAdapter(notificationList)
        notificationRecyclerView.adapter = notificationAdapter

        loadNotifications()
        startUpdatingNotifications()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopUpdatingNotifications() // Stop coroutine when the view is destroyed
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadNotifications() {
        // Example: Add some dummy notifications to the list
        notificationList.add(Notification("Bear", 12.34, 56.78, "12:30 PM"))
        notificationList.add(Notification("Elephant", 34.56, 78.90, "01:00 PM"))
        notificationAdapter.notifyDataSetChanged()

        // Update MapFragment with initial notifications
        updateMapFragment()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun startUpdatingNotifications() {
        updateJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val data = BluetoothManager.receiveData() ?: "Leopard,6.927079,79.861244" // Mocked data for testing

                Log.d("NotificationFragment", "Received data: $data")

                if (data.isNotEmpty()) {
                    try {
                        // Split the data into parts
                        val parts = data.split(",")
                        if (parts.size == 3) {
                            val animalType = parts[0].trim()
                            val latitude = parts[1].trim().toDoubleOrNull() ?: 0.0
                            val longitude = parts[2].trim().toDoubleOrNull() ?: 0.0

                            val timestamp = getCurrentTimestamp()
                            val notification = Notification(animalType, latitude, longitude, timestamp)

                            withContext(Dispatchers.Main) {
                                Log.d("NotificationFragment", "Received notification: $notification")
                                notificationList.add(notification)

                                // Ensure we only keep the latest five notifications
                                if (notificationList.size > 5) {
                                    notificationList.removeAt(0)
                                }

                                notificationAdapter.notifyDataSetChanged()
                                updateMapFragment()
                            }
                        } else {
                            Log.e("NotificationFragment", "Received data does not match expected format: $data")
                        }
                    } catch (e: Exception) {
                        Log.e("NotificationFragment", "Error parsing received data: ${e.message}")
                    }
                }
                delay(5000) // Wait for 5 seconds before receiving new data
            }
        }
    }

    private fun stopUpdatingNotifications() {
        updateJob?.cancel() // Cancel the coroutine job
    }

    private fun getCurrentTimestamp(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    /**
     * Updates the MapFragment with the latest five notifications.
     */
    private fun updateMapFragment() {
        // Pass the last five notifications to the MapFragment using the shared ViewModel
        sharedViewModel.updateMapNotifications(notificationList.takeLast(5))
    }
}
