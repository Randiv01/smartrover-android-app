package com.example.smartrover

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrover.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)
    }

    private fun setupListeners() {
        binding.btnSaveSettings.setOnClickListener {
            // TODO: Save to SharedPreferences or DataStore
            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnResetDefaults.setOnClickListener {
            // TODO: Reset settings logic
            binding.seekBarSensitivity.progress = 50
            binding.seekBarMaxSpeed.progress = 150
            binding.switchHaptics.isChecked = true
            binding.switchKeepScreenOn.isChecked = false
            Toast.makeText(this, "Defaults Restored", Toast.LENGTH_SHORT).show()
        }

        binding.btnCalibration.setOnClickListener {
            Toast.makeText(this, "Opening Calibration Tool...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
