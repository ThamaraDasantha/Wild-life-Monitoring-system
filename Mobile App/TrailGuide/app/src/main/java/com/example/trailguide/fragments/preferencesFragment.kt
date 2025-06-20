package com.example.trailguide.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.trailguide.R
import com.example.trailguide.utils.BluetoothManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class preferencesFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_prefernces, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Request location updates
        getLocation()

        // Find the Spinners in the layout
        val spinner1: Spinner = view.findViewById(R.id.Animal_spinner1)
        val spinner2: Spinner = view.findViewById(R.id.Animal_spinner2)
        val spinner3: Spinner = view.findViewById(R.id.Animal_spinner3)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Animals_array,
            android.R.layout.simple_spinner_item
        ).apply {
            // Specify the layout to use when the list of choices appears
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Apply the adapter to each spinner
        spinner1.adapter = adapter
        spinner2.adapter = adapter
        spinner3.adapter = adapter

        val submitButton: Button = view.findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            val selectedAnimal1 = spinner1.selectedItem.toString()
            val selectedAnimal2 = spinner2.selectedItem.toString()
            val selectedAnimal3 = spinner3.selectedItem.toString()

            val dataToSend = if (currentLocation != null) {
                "$selectedAnimal1,$selectedAnimal2,$selectedAnimal3,MOB_Location: ${currentLocation!!.latitude},${currentLocation!!.longitude}"
            } else {
                "$selectedAnimal1,$selectedAnimal2,$selectedAnimal3,Location:00000000"
            }

            if (BluetoothManager.isConnected) {
                if (BluetoothManager.sendData(dataToSend)) {
                    Toast.makeText(requireContext(), "Data sent to Trailguide", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to send data to Trailguide", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Not connected to Trailguide", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request location permissions if not already granted
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currentLocation = location
            } else {
                Toast.makeText(requireContext(), "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
