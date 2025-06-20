package com.example.trailguide.fragments

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trailguide.utils.BluetoothManager
import com.example.trailguide.R


class settingsfragment : Fragment() {

    private lateinit var connectButton: Button
    private lateinit var bluetoothStatusTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_settings, container, false)
        connectButton = view.findViewById(R.id.connect_button)
        bluetoothStatusTextView = view.findViewById(R.id.bluetooth_status)

        connectButton.setOnClickListener {
            if (BluetoothManager.isConnected) {
                Toast.makeText(requireContext(), "Already connected to Trailguide", Toast.LENGTH_SHORT).show()
            } else {
                findAndConnectToESP32()
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        updateConnectionStatus()
    }

    private fun updateConnectionStatus() {
        if (BluetoothManager.isConnected) {
            bluetoothStatusTextView.text = getString(R.string.Bluetooth_Connected)
            connectButton.isEnabled = false
        } else {
            bluetoothStatusTextView.text = getString(R.string.Bluetooth_Not_Connected)
            connectButton.isEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun findAndConnectToESP32() {
        val pairedDevices = BluetoothAdapter.getDefaultAdapter()?.bondedDevices
        val device = pairedDevices?.find { it.name == "Trailguide" } ?: return

        if (BluetoothManager.connectToDevice(device)) {
            Toast.makeText(requireContext(), "Connected to Trailguide", Toast.LENGTH_SHORT).show()
            updateConnectionStatus()
        } else {
            Toast.makeText(requireContext(), "Failed to connect", Toast.LENGTH_SHORT).show()
        }
    }
}
