package com.example.smartrover

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrover.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimations()

        // Transition to Onboarding or Main after delay
        binding.root.postDelayed({
            startActivity(Intent(this, Onboarding1Activity::class.java))
            finish()
        }, 3000)
    }

    private fun startAnimations() {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1500
        binding.layoutLogo.startAnimation(fadeIn)
    }
}
