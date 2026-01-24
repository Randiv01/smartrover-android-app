package com.example.smartrover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartrover.databinding.FragmentTelemetryBinding

class TelemetryFragment : Fragment() {

    private var _binding: FragmentTelemetryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelemetryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnExportData.setOnClickListener {
            Toast.makeText(context, "Exporting data...", Toast.LENGTH_SHORT).show()
        }
        
        startMockDataUpdates()
    }

    private fun startMockDataUpdates() {
        binding.root.postDelayed(object : Runnable {
            override fun run() {
                if (_binding == null) return
                val mockSpeed = (30..60).random()
                binding.tvSpeedValue.text = mockSpeed.toString()
                binding.root.postDelayed(this, 1000)
            }
        }, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
