package com.example.smartrover

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartrover.databinding.FragmentConnectionBinding

class ConnectionFragment : Fragment() {

    private var _binding: FragmentConnectionBinding? = null
    private val binding get() = _binding!!
    private var isBluetoothMode = true
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var pairedAdapter: BluetoothDeviceAdapter
    private lateinit var availableAdapter: BluetoothDeviceAdapter

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let { availableAdapter.addDevice(it) }
                }
                android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    binding.progressBarScanning.visibility = View.GONE
                    binding.btnScan.isEnabled = true
                    binding.btnScan.text = getString(R.string.scan_devices)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bluetoothManager = BluetoothManager(requireContext())
        setupRecyclerViews()
        setupListeners()
        updateUIMode()
        
        if (isBluetoothMode) {
            loadPairedDevices()
        }

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        requireActivity().registerReceiver(bluetoothReceiver, filter)
    }

    private fun setupRecyclerViews() {
        pairedAdapter = BluetoothDeviceAdapter { device -> connectToDevice(device) }
        binding.rvPairedDevices.layoutManager = LinearLayoutManager(context)
        binding.rvPairedDevices.adapter = pairedAdapter

        availableAdapter = BluetoothDeviceAdapter { device -> connectToDevice(device) }
        binding.rvAvailableDevices.layoutManager = LinearLayoutManager(context)
        binding.rvAvailableDevices.adapter = availableAdapter
    }

    private fun setupListeners() {
        binding.btnTypeBluetooth.setOnClickListener {
            if (!isBluetoothMode) {
                isBluetoothMode = true
                updateUIMode()
            }
        }

        binding.btnTypeWifi.setOnClickListener {
            if (isBluetoothMode) {
                isBluetoothMode = false
                updateUIMode()
            }
        }

        binding.btnScan.setOnClickListener {
            if (isBluetoothMode) {
                startBluetoothScan()
            } else {
                Toast.makeText(context, "Scanning WiFi...", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnConnect.setOnClickListener {
            val status = if (isBluetoothMode) "Connecting to Bluetooth..." else "Connecting to WiFi..."
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUIMode() {
        if (isBluetoothMode) {
            binding.btnTypeBluetooth.setBackgroundResource(R.drawable.btn_primary_selector)
            binding.btnTypeBluetooth.setTextColor(requireContext().getColor(R.color.white))
            binding.btnTypeWifi.setBackgroundResource(android.R.color.transparent)
            binding.btnTypeWifi.setTextColor(requireContext().getColor(R.color.text_secondary))
            binding.layoutManual.visibility = View.GONE
            binding.rvAvailableDevices.visibility = View.VISIBLE
            binding.btnScan.visibility = View.VISIBLE
        } else {
            binding.btnTypeWifi.setBackgroundResource(R.drawable.btn_primary_selector)
            binding.btnTypeWifi.setTextColor(requireContext().getColor(R.color.white))
            binding.btnTypeBluetooth.setBackgroundResource(android.R.color.transparent)
            binding.btnTypeBluetooth.setTextColor(requireContext().getColor(R.color.text_secondary))
            binding.layoutManual.visibility = View.VISIBLE
            binding.rvAvailableDevices.visibility = View.GONE
            binding.btnScan.visibility = View.GONE
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadPairedDevices() {
        val pairedDevices = bluetoothManager.getPairedDevices()
        pairedDevices?.let {
            pairedAdapter.updateDevices(it.toList())
        }
    }

    private fun startBluetoothScan() {
        if (!bluetoothManager.isBluetoothEnabled()) {
            Toast.makeText(context, "Please enable Bluetooth", Toast.LENGTH_SHORT).show()
            bluetoothManager.enableBluetooth()
            return
        }
        
        availableAdapter.clear()
        binding.progressBarScanning.visibility = View.VISIBLE
        binding.btnScan.isEnabled = false
        binding.btnScan.text = getString(R.string.status_scanning)
        bluetoothManager.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        Toast.makeText(context, "Connecting to ${device.name ?: device.address}...", Toast.LENGTH_SHORT).show()
        
        Thread {
            val success = bluetoothManager.connect(device.address)
            requireActivity().runOnUiThread {
                if (success) {
                    Toast.makeText(context, "Connected to ${device.name ?: device.address}", Toast.LENGTH_SHORT).show()
                    // Optionally navigate back or update UI
                } else {
                    Toast.makeText(context, "Connection failed", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().unregisterReceiver(bluetoothReceiver)
        bluetoothManager.stopDiscovery()
        _binding = null
    }
}
