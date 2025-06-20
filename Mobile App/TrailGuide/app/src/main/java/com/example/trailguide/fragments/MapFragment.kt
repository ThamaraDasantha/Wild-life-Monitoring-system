package com.example.trailguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import com.example.trailguide.R
import com.example.trailguide.models.Notification
import com.example.trailguide.viewmodels.SharedViewModel

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.activity_map, container, false)

        // Set up the map view
        mapView = rootView.findViewById(R.id.mapView)

        // Basic map settings
        mapView.setTileSource(TileSourceFactory.MAPNIK)  // Default tile source
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Display initial sample location
        addSampleLocationMarker()

        sharedViewModel.mapNotifications.observe(viewLifecycleOwner) { notifications ->
            updateMapWithNotifications(notifications)
        }

        return rootView
    }

    /**
     * Method to update the map with the latest five notifications.
     * It clears the existing markers, then adds markers for each of the last five notifications.
     */
    fun updateMapWithNotifications(notifications: List<Notification>) {
        mapView.overlays.clear() // Clear existing markers

        // Add markers for each of the latest five notifications
        notifications.takeLast(5).forEach { notification ->
            val marker = Marker(mapView)
            marker.position = GeoPoint(notification.latitude, notification.longitude)
            marker.title = "${notification.animalType} sighted at ${notification.timestamp}"
            mapView.overlays.add(marker)
        }

        // Add the sample location marker again after clearing overlays
        addSampleLocationMarker()

        // Refresh the map view
        mapView.invalidate()
    }

    /**
     * Adds a sample location marker for Yala National Park.
     */
    private fun addSampleLocationMarker() {
        // Sample location: Yala National Park coordinates
        val sampleMarker = Marker(mapView)
        sampleMarker.position = GeoPoint(6.464185, 81.471847)
        sampleMarker.title = "Yala National Park"
        mapView.overlays.add(sampleMarker)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume() // Important to call this for the map to load properly
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause() // Important to call this to save resources
    }
}
