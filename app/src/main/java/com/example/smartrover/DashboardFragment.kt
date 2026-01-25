package com.example.smartrover

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.smartrover.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var bluetoothManager: BluetoothManager
    private var isManualMode = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        bluetoothManager = BluetoothManager.getInstance(requireContext())
        setupUI()
        
        // Initial Connection UI update
        updateConnectionUI(bluetoothManager.isConnected(), bluetoothManager.getConnectedDeviceName())
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
                    }
                    R.id.btnAuto -> {
                        isManualMode = false
                        bluetoothManager.sendCommand("A")
                        updateControlButtonsState(false)
                    }
                }
            }
        }
        binding.toggleMode.check(R.id.btnManual)

        // Connect Button Redirection
        binding.btnConnect.setOnClickListener {
            (activity as? MainActivity)?.loadFragment(ConnectionFragment(), "Connection Setup", false)
        }

        // Directional Buttons
        binding.btnForward.setOnClickListener { sendCommandIfManual("F") }
        binding.btnBackward.setOnClickListener { sendCommandIfManual("B") }
        binding.btnLeft.setOnClickListener { sendCommandIfManual("L") }
        binding.btnRight.setOnClickListener { sendCommandIfManual("R") }
        binding.btnStop.setOnClickListener { sendCommandIfManual("S") }

        // Extra Controls
        binding.btnHorn.setOnClickListener { bluetoothManager.sendCommand("H") }
        
        var isLightsOn = false
        binding.btnLights.setOnClickListener { 
            isLightsOn = !isLightsOn
            bluetoothManager.sendCommand("W")
            binding.btnLights.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(), if (isLightsOn) R.color.accent_yellow else R.color.button_yellow_off
            )
        }
        
        // Speed Slider
        binding.seekBarDashSpeed.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.tvSpeedVal.text = "Speed: $progress"
                    bluetoothManager.sendCommand(progress.toString())
                }
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }

    fun updateConnectionUI(connected: Boolean, deviceName: String?) {
        if (_binding == null) return
        
        if (connected) {
            binding.tvConnectionStatus.text = "Online ($deviceName)"
            binding.tvConnectionStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_green))
            binding.ivConnIcon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.success_green)
            binding.btnConnect.text = "DISCONNECT"
        } else {
            binding.tvConnectionStatus.text = "Offline"
            binding.tvConnectionStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_offline))
            binding.ivConnIcon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary_blue)
            binding.btnConnect.text = "CONNECT"
        }
    }

    private fun sendCommandIfManual(command: String) {
        if (!bluetoothManager.isConnected()) {
            Toast.makeText(context, "Please connect to Rover first", Toast.LENGTH_SHORT).show()
            return
        }
        if (isManualMode) {
            bluetoothManager.sendCommand(command)
        } else {
            Toast.makeText(context, "Switch to Manual Mode", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateControlButtonsState(enabled: Boolean) {
        val views = listOf(binding.btnForward, binding.btnBackward, binding.btnLeft, binding.btnRight, binding.btnStop)
        views.forEach { 
            it.isEnabled = enabled
            it.alpha = if (enabled) 1.0f else 0.5f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
