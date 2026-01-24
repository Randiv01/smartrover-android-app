package com.example.smartrover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartrover.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSaveSettings.setOnClickListener {
            Toast.makeText(context, "Settings Saved", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }

        binding.btnResetDefaults.setOnClickListener {
            binding.seekBarSensitivity.progress = 50
            binding.seekBarMaxSpeed.progress = 150
            binding.switchHaptics.isChecked = true
            binding.switchKeepScreenOn.isChecked = false
            Toast.makeText(context, "Defaults Restored", Toast.LENGTH_SHORT).show()
        }

        binding.btnCalibration.setOnClickListener {
            Toast.makeText(context, "Opening Calibration Tool...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
