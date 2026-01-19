package com.example.smartrover

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.smartrover.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var bluetoothManager: BluetoothManager
    private var isManualMode = true // Default is manual

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bluetoothManager = BluetoothManager(this)

        checkAndRequestPermissions()
        setupUI()
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSION_REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupUI() {
        // Mode Selection
        binding.toggleMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnManual -> {
                        isManualMode = true
                        bluetoothManager.sendCommand("M")
                        updateControlButtonsState(true)
                        updateModeHighlight(true)
                    }
                    R.id.btnAuto -> {
                        isManualMode = false
                        bluetoothManager.sendCommand("A")
                        updateControlButtonsState(false)
                        updateModeHighlight(false)
                    }
                }
            }
        }
        // Default selection
        binding.toggleMode.check(R.id.btnManual)
        updateModeHighlight(true) // Initial highlight

        // Connect Button
        binding.btnConnect.setOnClickListener {
            if (!bluetoothManager.isBluetoothEnabled()) {
                Toast.makeText(this, "Please enable Bluetooth first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showPairedDevicesDialog()
        }

        // Directional Buttons
        binding.btnForward.setOnClickListener { sendCommandIfManual("F") }
        binding.btnBackward.setOnClickListener { sendCommandIfManual("B") }
        binding.btnLeft.setOnClickListener { sendCommandIfManual("L") }
        binding.btnRight.setOnClickListener { sendCommandIfManual("R") }
        binding.btnStop.setOnClickListener { sendCommandIfManual("S") }

        // Extra Controls
        binding.btnHorn.setOnClickListener { bluetoothManager.sendCommand("H") }
        
        // Light Toggle (Simple toggle logic)
        var isLightsOn = false
        binding.btnLights.setOnClickListener { 
            isLightsOn = !isLightsOn
            bluetoothManager.sendCommand("W") // Assuming W toggles or W/w logic. Sending W for now.
             // Update icon tint if needed, or leave as simple press
        }
        
        // Speed Slider
        binding.seekBarSpeed.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Send speed value (0-9)
                    bluetoothManager.sendCommand(progress.toString())
                }
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }
    
    private fun updateModeHighlight(isManual: Boolean) {
        val activeColor = ContextCompat.getColor(this, R.color.blue_accent)
        val inactiveColor = android.graphics.Color.TRANSPARENT
        
        if (isManual) {
            binding.btnManual.setBackgroundColor(activeColor)
            binding.btnAuto.setBackgroundColor(inactiveColor)
        } else {
            binding.btnManual.setBackgroundColor(inactiveColor)
            binding.btnAuto.setBackgroundColor(activeColor)
        }
    }

    private fun sendCommandIfManual(command: String) {
        if (isManualMode) {
            bluetoothManager.sendCommand(command)
        } else {
            Toast.makeText(this, "Switch to Manual Mode to control", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateControlButtonsState(enabled: Boolean) {
        binding.btnForward.isEnabled = enabled
        binding.btnBackward.isEnabled = enabled
        binding.btnLeft.isEnabled = enabled
        binding.btnRight.isEnabled = enabled
        binding.btnStop.isEnabled = enabled
        
        // Visual feedback for disabled state
        val alpha = if (enabled) 1.0f else 0.5f
        binding.btnForward.alpha = alpha
        binding.btnBackward.alpha = alpha
        binding.btnLeft.alpha = alpha
        binding.btnRight.alpha = alpha
        binding.btnStop.alpha = alpha
    }

    @SuppressLint("MissingPermission")
    private fun showPairedDevicesDialog() {
        val devices = bluetoothManager.getPairedDevices()
        if (devices.isNullOrEmpty()) {
            Toast.makeText(this, "No paired devices found", Toast.LENGTH_SHORT).show()
            return
        }

        val deviceList = devices.toList()
        val deviceNames = deviceList.map { "${it.name} (${it.address})" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Robot Car")
            .setItems(deviceNames) { _, which ->
                connectToDevice(deviceList[which])
            }
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        binding.tvConnectionStatus.text = "Connecting to ${device.name}..."
        
        Thread {
            val success = bluetoothManager.connect(device.address)
            runOnUiThread {
                if (success) {
                    binding.tvConnectionStatus.text = "Connected to ${device.name}"
                    binding.tvConnectionStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
                    Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show()
                } else {
                    binding.tvConnectionStatus.text = "Connection Failed"
                    binding.tvConnectionStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                    Toast.makeText(this, "Failed to connect", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothManager.disconnect()
    }
}
