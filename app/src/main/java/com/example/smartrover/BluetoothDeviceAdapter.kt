package com.example.smartrover

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BluetoothDeviceAdapter(
    private val onDeviceClicked: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>() {

    private val devices = mutableListOf<BluetoothDevice>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvDeviceName)
        val tvAddress: TextView = view.findViewById(R.id.tvDeviceAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bluetooth_device, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices[position]
        holder.tvName.text = device.name ?: "Unknown Device"
        holder.tvAddress.text = device.address
        holder.itemView.setOnClickListener { onDeviceClicked(device) }
    }

    override fun getItemCount() = devices.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateDevices(newDevices: List<BluetoothDevice>) {
        devices.clear()
        devices.addAll(newDevices)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addDevice(device: BluetoothDevice) {
        if (!devices.contains(device)) {
            devices.add(device)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        devices.clear()
        notifyDataSetChanged()
    }
}
