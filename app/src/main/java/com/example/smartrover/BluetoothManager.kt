package com.example.smartrover

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class BluetoothManager private constructor(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    private var outStream: OutputStream? = null
    private var connectedDeviceName: String? = null
    
    // UUID for Serial Port Profile (SPP) which is used by HC-05/HC-06
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    var onConnectionStateChanged: ((Boolean, String?) -> Unit)? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: BluetoothManager? = null

        fun getInstance(context: Context): BluetoothManager {
            if (instance == null) {
                instance = BluetoothManager(context.applicationContext)
            }
            return instance!!
        }
    }

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
            outStream = socket?.outputStream
            connectedDeviceName = device.name ?: device.address
            
            onConnectionStateChanged?.invoke(true, connectedDeviceName)
            return true
        } catch (e: IOException) {
            Log.e("BluetoothManager", "Connection failed", e)
            cleanup()
            onConnectionStateChanged?.invoke(false, null)
            return false
        }
    }

    fun sendCommand(command: String) {
        if (outStream == null) return
        try {
            outStream?.write(command.toByteArray())
        } catch (e: IOException) {
            Log.e("BluetoothManager", "Error sending command", e)
            cleanup()
            onConnectionStateChanged?.invoke(false, null)
        }
    }

    fun disconnect() {
        cleanup()
        onConnectionStateChanged?.invoke(false, null)
    }

    private fun cleanup() {
        try {
            outStream?.close()
            socket?.close()
        } catch (e: IOException) {
            Log.e("BluetoothManager", "Error during cleanup", e)
        }
        outStream = null
        socket = null
        connectedDeviceName = null
    }

    fun isConnected(): Boolean {
        return socket?.isConnected == true
    }
    
    fun getConnectedDeviceName(): String? = connectedDeviceName
}
