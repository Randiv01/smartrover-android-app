package com.example.smartrover

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrover.databinding.ActivityOnboarding1Binding

class Onboarding1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboarding1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboarding1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listener for the button using view binding
        binding.btnLetsGo.setOnClickListener {
            // Start the home activity
            startActivity(Intent(this, HomeActivity::class.java))
            // Finish the onboarding activity so user can't go back to it
            finish()
        }
    }
}