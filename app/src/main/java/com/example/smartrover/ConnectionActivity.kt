package com.example.smartrover

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrover.databinding.ActivityConnectionBinding

class ConnectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectionBinding
    private var isBluetoothMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        updateUIMode()
    }

    private fun setupListeners() {
        // Type Toggles
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

        // Scan/Connect Actions
        binding.btnScan.setOnClickListener {
            if (isBluetoothMode) {
                startBluetoothScan()
            } else {
                // WiFi scan or refresh logic
                Toast.makeText(this, "Scanning WiFi...", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnConnect.setOnClickListener {
            val status = if (isBluetoothMode) "Connecting to Bluetooth Device..." else "Connecting to ${binding.etIpAddress.text}..."
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
            // TODO: Implement actual connection logic
        }
    }

    private fun updateUIMode() {
        if (isBluetoothMode) {
            binding.btnTypeBluetooth.setBackgroundResource(R.drawable.btn_primary_selector)
            binding.btnTypeBluetooth.setTextColor(getColor(R.color.white))
            
            binding.btnTypeWifi.setBackgroundResource(android.R.color.transparent)
            binding.btnTypeWifi.setTextColor(getColor(R.color.text_secondary))

            binding.layoutManual.visibility = View.GONE
            binding.rvAvailableDevices.visibility = View.VISIBLE
            binding.tvAvailableLabel.visibility = View.VISIBLE
            binding.btnScan.visibility = View.VISIBLE
        } else {
            binding.btnTypeWifi.setBackgroundResource(R.drawable.btn_primary_selector)
            binding.btnTypeWifi.setTextColor(getColor(R.color.white))
            
            binding.btnTypeBluetooth.setBackgroundResource(android.R.color.transparent)
            binding.btnTypeBluetooth.setTextColor(getColor(R.color.text_secondary))

            binding.layoutManual.visibility = View.VISIBLE
            binding.rvAvailableDevices.visibility = View.GONE
            binding.tvAvailableLabel.visibility = View.GONE
            binding.btnScan.visibility = View.GONE
        }
    }

    private fun startBluetoothScan() {
        binding.progressBarScanning.visibility = View.VISIBLE
        binding.btnScan.isEnabled = false
        binding.btnScan.text = getString(R.string.status_scanning)
        
        // Mocking scan finish after 2 seconds
        binding.root.postDelayed({
            binding.progressBarScanning.visibility = View.GONE
            binding.btnScan.isEnabled = true
            binding.btnScan.text = getString(R.string.scan_devices)
        }, 2000)
    }
}
