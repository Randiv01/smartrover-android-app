package com.example.smartrover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartrover.databinding.FragmentConnectionBinding

class ConnectionFragment : Fragment() {

    private var _binding: FragmentConnectionBinding? = null
    private val binding get() = _binding!!
    private var isBluetoothMode = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        updateUIMode()
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

    private fun startBluetoothScan() {
        binding.progressBarScanning.visibility = View.VISIBLE
        binding.btnScan.isEnabled = false
        binding.btnScan.text = "Scanning..."
        binding.root.postDelayed({
            if (_binding != null) {
                binding.progressBarScanning.visibility = View.GONE
                binding.btnScan.isEnabled = true
                binding.btnScan.text = "SCAN DEVICES"
            }
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
