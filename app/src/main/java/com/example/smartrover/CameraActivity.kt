package com.example.smartrover

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrover.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnCapture.setOnClickListener {
            Toast.makeText(this, "Photo captured!", Toast.LENGTH_SHORT).show()
        }

        binding.btnRecord.setOnClickListener {
            Toast.makeText(this, "Video recording started...", Toast.LENGTH_SHORT).show()
        }
    }
}
