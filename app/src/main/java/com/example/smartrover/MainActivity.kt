package com.example.smartrover

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.smartrover.databinding.ActivityMainBinding
import com.example.smartrover.databinding.NavHeaderMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var toggle: ActionBarDrawerToggle? = null
    private lateinit var bluetoothManager: BluetoothManager

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bluetoothManager = BluetoothManager.getInstance(this)
        checkAndRequestPermissions()
        checkBluetoothEnabled()

        setupNavigation()

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment(), "Smart Rover", true)
        }
        
        // Handle back stack changes to update toolbar
        supportFragmentManager.addOnBackStackChangedListener {
            updateToolbarForCurrentFragment()
        }

        // Setup connection observation for header
        bluetoothManager.onConnectionStateChanged = { connected, deviceName ->
            runOnUiThread {
                updateHeaderStatus(connected, deviceName)
                // Also update current fragment if it's the Dashboard
                val current = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                if (current is DashboardFragment) {
                    current.updateConnectionUI(connected, deviceName)
                }
            }
        }

        // Migrate to OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
                } else if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun updateHeaderStatus(connected: Boolean, deviceName: String?) {
        val headerView = binding.navView.getHeaderView(0)
        val tvStatus = headerView.findViewById<TextView>(R.id.tvHeaderStatus)
        if (connected) {
            tvStatus.text = "Status: Online ($deviceName)"
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.success_green))
        } else {
            tvStatus.text = "Status: Disconnected"
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        }
    }

    private fun checkBluetoothEnabled() {
        if (!bluetoothManager.isBluetoothEnabled()) {
            Toast.makeText(this, "Bluetooth is disabled. Please enable it for full functionality.", Toast.LENGTH_LONG).show()
            bluetoothManager.enableBluetooth()
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSION_REQUEST_CODE)
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
        
        // Initial header update
        updateHeaderStatus(bluetoothManager.isConnected(), bluetoothManager.getConnectedDeviceName())
    }

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
            is DashboardFragment -> {
                binding.toolbar.title = "Smart Rover"
                binding.navView.setCheckedItem(R.id.nav_dashboard)
            }
            is TelemetryFragment -> {
                binding.toolbar.title = "Telemetry"
                binding.navView.setCheckedItem(R.id.nav_telemetry)
            }
            is SettingsFragment -> {
                binding.toolbar.title = "Settings"
                binding.navView.setCheckedItem(R.id.nav_settings)
            }
            is ConnectionFragment -> {
                binding.toolbar.title = "Connection Setup"
                binding.navView.setCheckedItem(R.id.nav_connection)
            }
            is HelpFragment -> {
                binding.toolbar.title = "Help & Support"
                binding.navView.setCheckedItem(R.id.nav_help)
            }
            is AutoModeFragment -> {
                binding.toolbar.title = "Auto Mode Config"
                binding.navView.setCheckedItem(R.id.nav_auto_config)
            }
            is AboutFragment -> {
                binding.toolbar.title = "About"
                binding.navView.setCheckedItem(R.id.nav_about)
            }
        }

        // Hamburger icon visibility on all primary pages
        val isPrimaryPage = currentFragment is DashboardFragment || 
                           currentFragment is ConnectionFragment ||
                           currentFragment is AutoModeFragment ||
                           currentFragment is TelemetryFragment ||
                           currentFragment is HelpFragment ||
                           currentFragment is SettingsFragment ||
                           currentFragment is AboutFragment

        if (isPrimaryPage) {
            toggle?.isDrawerIndicatorEnabled = true
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            binding.bottomNavigation.visibility = if (currentFragment is DashboardFragment || 
                                                     currentFragment is TelemetryFragment) View.VISIBLE else View.GONE
            
            binding.toolbar.setNavigationOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        } else {
            toggle?.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.bottomNavigation.visibility = View.GONE
            
            binding.toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
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
            R.id.nav_about -> loadFragment(AboutFragment(), "About", false)
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
}
