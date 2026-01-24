package com.example.smartrover

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrover.databinding.ActivityTelemetryBinding

class TelemetryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTelemetryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelemetryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        startMockDataUpdates()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.telemetry_title)
    }

    private fun setupListeners() {
        binding.btnExportData.setOnClickListener {
            Toast.makeText(this, "Exporting data to CSV...", Toast.LENGTH_SHORT).show()
        }

        binding.btnClearStats.setOnClickListener {
            Toast.makeText(this, "Statistics cleared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startMockDataUpdates() {
        // Mock data updates for demonstration
        binding.root.postDelayed(object : Runnable {
            override fun run() {
                val mockSpeed = (30..60).random()
                val mockDist = (100..200).random()
                binding.tvSpeedValue.text = mockSpeed.toString()
                binding.tvDistanceValue.text = mockDist.toString()
                binding.root.postDelayed(this, 1000)
            }
        }, 1000)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
