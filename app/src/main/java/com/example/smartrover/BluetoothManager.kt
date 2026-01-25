package com.example.smartrover

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import java.io.IOException
import java.util.UUID

class BluetoothManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    
    // UUID for Serial Port Profile (SPP) which is used by HC-05/HC-06
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    var onDeviceFound: ((BluetoothDevice) -> Unit)? = null
    var onDiscoveryFinished: (() -> Unit)? = null

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    @SuppressLint("MissingPermission")
    fun enableBluetooth() {
        bluetoothAdapter?.enable()
    }

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): Set<BluetoothDevice>? {
        return try {
            bluetoothAdapter?.bondedDevices
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @SuppressLint("MissingPermission")
    fun startDiscovery() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter?.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun connect(deviceAddress: String): Boolean {
        if (bluetoothAdapter == null) return false
        
        try {
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket?.connect()
            return true
        } catch (e: IOException) {
            Log.e("BluetoothManager", "Connection failed", e)
            try {
                socket?.close()
            } catch (closeException: IOException) {
                Log.e("BluetoothManager", "Could not close socket", closeException)
            }
            return false
        }
    }

    fun sendCommand(command: String) {
        if (socket == null) return
        try {
            socket?.outputStream?.write(command.toByteArray())
        } catch (e: IOException) {
            Log.e("BluetoothManager", "Error sending command", e)
        }
    }

    fun disconnect() {
        try {
            socket?.close()
        } catch (e: IOException) {
            Log.e("BluetoothManager", "Error closing socket", e)
        }
    }

    fun isConnected(): Boolean {
        return socket?.isConnected == true
    }
}
