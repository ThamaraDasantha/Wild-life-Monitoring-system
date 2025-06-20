package com.example.trailguide.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

object BluetoothManager {
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    var isConnected = false
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private const val TAG = "BluetoothManager"

    // Connect to the Bluetooth device
    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice): Boolean {
        return try {
            // Create socket and connect
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()

            // Open input and output streams
            inputStream = bluetoothSocket?.inputStream
            outputStream = bluetoothSocket?.outputStream

            // Set connection status
            isConnected = true
            Log.d(TAG, "Connected to device: ${device.address}")
            true
        } catch (e: IOException) {
            Log.e(TAG, "Error connecting to device", e)
            disconnect()
            false
        }
    }

    // Disconnect the Bluetooth socket
    fun disconnect() {
        try {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
            isConnected = false
            bluetoothSocket = null
            inputStream = null
            outputStream = null
            Log.d(TAG, "Disconnected")
        } catch (e: IOException) {
            Log.e(TAG, "Error closing Bluetooth resources", e)
        }
    }

    // Send data via Bluetooth
    fun sendData(data: String): Boolean {
        return try {
            // Check if the socket is connected
            if (isConnected) {
                outputStream?.write(data.toByteArray())
                Log.d(TAG, "Sent data: $data")
                true
            } else {
                Log.e(TAG, "Bluetooth is not connected.")
                false
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error sending data", e)
            false
        }
    }

    // Receive data from Bluetooth
    fun receiveData(): String? {
        return try {
            val buffer = ByteArray(1024)
            val bytesRead = inputStream?.read(buffer) ?: -1

            if (bytesRead > 0) {
                val receivedData = String(buffer, 0, bytesRead)
                Log.d(TAG, "Received data: $receivedData")
                receivedData
            } else {
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error receiving data", e)
            null
        }
    }
}
