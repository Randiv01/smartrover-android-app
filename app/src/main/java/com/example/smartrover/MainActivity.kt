package com.example.smartrover

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.smartrover.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var toggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment(), "Smart Rover", true)
        }
        
        // Handle back stack changes to update toolbar
        supportFragmentManager.addOnBackStackChangedListener {
            updateToolbarForCurrentFragment()
        }
    }

    private fun setupNavigation() {
        setSupportActionBar(binding.toolbar)

        // Setup Drawer Toggle
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.nav_open, R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle!!)
        toggle!!.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        // Bottom Nav Icons/Selection
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(DashboardFragment(), "Smart Rover", true)
                R.id.nav_camera -> startActivity(Intent(this, CameraActivity::class.java))
                R.id.nav_stats -> loadFragment(TelemetryFragment(), "Telemetry", true)
                R.id.nav_settings -> loadFragment(SettingsFragment(), "Settings", false)
            }
            true
        }
    }

    /**
     * Loads a fragment into the container.
     * @param clearStack If true, clears the entire back stack (used for primary sections)
     */
    fun loadFragment(fragment: Fragment, title: String, clearStack: Boolean) {
        if (clearStack) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        if (!clearStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
        
        binding.toolbar.title = title
    }

    private fun updateToolbarForCurrentFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        
        // Update Title
        when (currentFragment) {
            is DashboardFragment -> binding.toolbar.title = "Smart Rover"
            is TelemetryFragment -> binding.toolbar.title = "Telemetry"
            is SettingsFragment -> binding.toolbar.title = "Settings"
            is ConnectionFragment -> binding.toolbar.title = "Connection Setup"
            is HelpFragment -> binding.toolbar.title = "Help & Support"
            is AutoModeFragment -> binding.toolbar.title = "Auto Mode Config"
        }

        // Handle Back Arrow vs Hamburger
        val isRoot = supportFragmentManager.backStackEntryCount == 0
        if (isRoot) {
            toggle?.isDrawerIndicatorEnabled = true
            binding.bottomNavigation.visibility = View.VISIBLE
        } else {
            toggle?.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.bottomNavigation.visibility = View.GONE // Optional: hide bottom nav on sub-pages
            
            binding.toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
        toggle?.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> loadFragment(DashboardFragment(), "Smart Rover", true)
            R.id.nav_connection -> loadFragment(ConnectionFragment(), "Connection Setup", false)
            R.id.nav_telemetry -> loadFragment(TelemetryFragment(), "Telemetry", true)
            R.id.nav_auto_config -> loadFragment(AutoModeFragment(), "Auto Mode Config", false)
            R.id.nav_help -> loadFragment(HelpFragment(), "Help & Support", false)
            R.id.nav_settings -> loadFragment(SettingsFragment(), "Settings", false)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle?.onOptionsItemSelected(item) == true) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}