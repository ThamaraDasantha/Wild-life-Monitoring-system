package com.example.trailguide

import android.app.Application
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize OSMDroid configuration
        initializeOSMDroid()
    }

    private fun initializeOSMDroid() {
        // Initialize OSMDroid configuration
        Configuration.getInstance().load(this, this.getSharedPreferences("OSMDroidPrefs", MODE_PRIVATE))

        // Set the user agent string (optional but recommended)
        Configuration.getInstance().userAgentValue = "TrailGuideApp/1.0"

        // Set up the offline map cache directory
        val offlineMapDir = File(filesDir, "osmdroid")
        if (!offlineMapDir.exists()) {
            offlineMapDir.mkdirs() // Create directory if it doesn't exist
        }

        // Set the tile source to OpenStreetMap's Mapnik tiles
        val tileSource: ITileSource = TileSourceFactory.MAPNIK

        // Create a MapTileProvider for offline map tiles
        val mapTileProvider = MapTileProviderBasic(applicationContext, tileSource)

        // Use MapView to load the map tiles (no setTileProvider method available)
        val mapView = MapView(applicationContext)
        mapView.setTileSource(tileSource)

        // Set the map center to a specific location (latitude, longitude)
        val markerLocation = GeoPoint(6.464185, 81.471847)  // Sample coordinates (Los Angeles, CA)
        mapView.controller.setCenter(markerLocation)

        // Optionally set the zoom level
        mapView.controller.setZoom(12)  // Adjust zoom level as needed

        // Add a marker on the map at the specified coordinates
        val marker = Marker(mapView)
        marker.position = markerLocation
        marker.title = "Sample Marker"
        mapView.overlays.add(marker)  // Add the marker to the map
    }
}
