# Smart Rover - Activity Code Implementation Guide

This guide provides the essential Kotlin/Java code snippets to implement the interactive features of the enhanced Smart Rover dashboard.

## 1. Initialize Views in onCreate()

```kotlin
class HomeActivity : AppCompatActivity() {
    
    // Status Bar
    private lateinit var tvBatteryLevel: TextView
    private lateinit var ivBattery: ImageView
    private lateinit var tvSignalStrength: TextView
    private lateinit var tvConnectionStatus: TextView
    
    // Control Buttons
    private lateinit var btnLeft: AppCompatButton
    private lateinit var btnRight: AppCompatButton
    private lateinit var btnForward: AppCompatButton
    private lateinit var btnBackward: AppCompatButton
    private lateinit var btnStop: AppCompatButton
    
    // Extra Controls
    private lateinit var btnConnect: MaterialButton
    private lateinit var btnHorn: MaterialButton
    private lateinit var btnLights: MaterialButton
    
    // Mode Selection
    private lateinit var toggleMode: MaterialButtonToggleGroup
    private lateinit var btnManual: MaterialButton
    private lateinit var btnAuto: MaterialButton
    private lateinit var tvAutoModeStatus: TextView
    
    // Speed Control
    private lateinit var seekBarSpeed: SeekBar
    private lateinit var tvSpeedValue: TextView
    
    private var isLightsOn = false
    private var isConnected = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        // Initialize views
        initializeViews()
        
        // Setup listeners
        setupControlListeners()
        setupModeToggle()
        setupSpeedControl()
        setupExtraControls()
    }
    
    private fun initializeViews() {
        // Status Bar
        tvBatteryLevel = findViewById(R.id.tvBatteryLevel)
        ivBattery = findViewById(R.id.ivBattery)
        tvSignalStrength = findViewById(R.id.tvSignalStrength)
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus)
        
        // Control Buttons
        btnLeft = findViewById(R.id.btnLeft)
        btnRight = findViewById(R.id.btnRight)
        btnForward = findViewById(R.id.btnForward)
        btnBackward = findViewById(R.id.btnBackward)
        btnStop = findViewById(R.id.btnStop)
        
        // Extra Controls
        btnConnect = findViewById(R.id.btnConnect)
        btnHorn = findViewById(R.id.btnHorn)
        btnLights = findViewById(R.id.btnLights)
        
        // Mode
        toggleMode = findViewById(R.id.toggleMode)
        btnManual = findViewById(R.id.btnManual)
        btnAuto = findViewById(R.id.btnAuto)
        tvAutoModeStatus = findViewById(R.id.tvAutoModeStatus)
        
        // Speed
        seekBarSpeed = findViewById(R.id.seekBarSpeed)
        tvSpeedValue = findViewById(R.id.tvSpeedValue)
        
        // Set Manual mode as default
        toggleMode.check(R.id.btnManual)
    }
}
```

## 2. Connection Status Management

```kotlin
private fun updateConnectionStatus(connected: Boolean) {
    isConnected = connected
    
    if (connected) {
        tvConnectionStatus.text = "ONLINE"
        tvConnectionStatus.setTextColor(getColor(R.color.status_online))
        btnConnect.text = "Disconnect"
        
        // Update signal strength (example - you can get actual signal strength)
        tvSignalStrength.text = "BT • Strong"
        
    } else {
        tvConnectionStatus.text = "OFFLINE"
        tvConnectionStatus.setTextColor(getColor(R.color.status_offline))
        btnConnect.text = "Connect"
        
        tvSignalStrength.text = "BT • No signal"
    }
}
```

## 3. Mode Toggle Implementation

```kotlin
private fun setupModeToggle() {
    toggleMode.addOnButtonCheckedListener { group, checkedId, isChecked ->
        if (isChecked) {
            when (checkedId) {
                R.id.btnManual -> {
                    enableManualControls(true)
                    tvAutoModeStatus.visibility = View.GONE
                    // Send 'M' command to Arduino for Manual mode
                    sendBluetoothCommand("M")
                }
                R.id.btnAuto -> {
                    enableManualControls(false)
                    tvAutoModeStatus.visibility = View.VISIBLE
                    tvAutoModeStatus.text = "Auto mode active"
                    // Send 'A' command to Arduino for Auto mode
                    sendBluetoothCommand("A")
                }
            }
        }
    }
}

private fun enableManualControls(enabled: Boolean) {
    btnLeft.isEnabled = enabled
    btnRight.isEnabled = enabled
    btnForward.isEnabled = enabled
    btnBackward.isEnabled = enabled
    
    // Visual feedback through alpha
    val alpha = if (enabled) 1.0f else 0.5f
    btnLeft.alpha = alpha
    btnRight.alpha = alpha
    btnForward.alpha = alpha
    btnBackward.alpha = alpha
}

// Call this method when receiving status updates from Arduino in AUTO mode
private fun updateAutoModeStatus(status: String) {
    if (toggleMode.checkedButtonId == R.id.btnAuto) {
        tvAutoModeStatus.visibility = View.VISIBLE
        tvAutoModeStatus.text = status
        // Examples: "Avoiding obstacle", "Following path", "Idle", "Turning left", etc.
    }
}
```

## 4. Control Buttons Setup

```kotlin
private fun setupControlListeners() {
    // Forward
    btnForward.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                sendBluetoothCommand("F") // Forward
                true
            }
            MotionEvent.ACTION_UP -> {
                sendBluetoothCommand("S") // Stop when released
                true
            }
            else -> false
        }
    }
    
    // Backward
    btnBackward.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                sendBluetoothCommand("B") // Backward
                true
            }
            MotionEvent.ACTION_UP -> {
                sendBluetoothCommand("S") // Stop
                true
            }
            else -> false
        }
    }
    
    // Left
    btnLeft.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                sendBluetoothCommand("L") // Left
                true
            }
            MotionEvent.ACTION_UP -> {
                sendBluetoothCommand("S") // Stop
                true
            }
            else -> false
        }
    }
    
    // Right
    btnRight.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                sendBluetoothCommand("R") // Right
                true
            }
            MotionEvent.ACTION_UP -> {
                sendBluetoothCommand("S") // Stop
                true
            }
            else -> false
        }
    }
    
    // Stop button
    btnStop.setOnClickListener {
        sendBluetoothCommand("S") // Emergency stop
    }
}
```

## 5. Speed Control Implementation

```kotlin
private fun setupSpeedControl() {
    seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            // Update speed display
            tvSpeedValue.text = progress.toString()
        }
        
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            // Send speed command to Arduino when user finishes adjusting
            val speed = seekBar?.progress ?: 5
            sendBluetoothCommand("${speed}") // Send speed value (0-9)
        }
    })
}
```

## 6. Extra Controls (Horn, Lights, Connect)

```kotlin
private fun setupExtraControls() {
    // Connect Button
    btnConnect.setOnClickListener {
        if (isConnected) {
            disconnectBluetooth()
        } else {
            showBluetoothDeviceList()
        }
    }
    
    // Horn Button - Visual feedback with animation
    btnHorn.setOnClickListener {
        sendBluetoothCommand("H") // Horn command
        
        // Pulse animation
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            btnHorn,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        ).apply {
            duration = 100
        }
        
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            btnHorn,
            PropertyValuesHolder.ofFloat("scaleX", 1.0f),
            PropertyValuesHolder.ofFloat("scaleY", 1.0f)
        ).apply {
            duration = 100
        }
        
        AnimatorSet().apply {
            playSequentially(scaleUp, scaleDown)
            start()
        }
    }
    
    // Lights Button - Toggle ON/OFF with color change
    btnLights.setOnClickListener {
        isLightsOn = !isLightsOn
        
        if (isLightsOn) {
            btnLights.backgroundTintList = ColorStateList.valueOf(getColor(R.color.button_yellow))
            sendBluetoothCommand("W") // Lights ON
        } else {
            btnLights.backgroundTintList = ColorStateList.valueOf(getColor(R.color.button_yellow_off))
            sendBluetoothCommand("w") // Lights OFF
        }
    }
}

private fun showBluetoothDeviceList() {
    // Implementation to show paired Bluetooth devices
    // This would typically show an AlertDialog with the list of devices
    // Example implementation would go here
}

private fun disconnectBluetooth() {
    // Close Bluetooth connection
    updateConnectionStatus(false)
}
```

## 7. Battery Level Updates

```kotlin
// Call this periodically or when receiving battery updates from Arduino
private fun updateBatteryLevel(percentage: Int) {
    tvBatteryLevel.text = "$percentage%"
    
    // Change battery icon color based on level
    when {
        percentage > 50 -> ivBattery.setColorFilter(getColor(R.color.status_online))
        percentage > 20 -> ivBattery.setColorFilter(getColor(R.color.button_yellow))
        else -> ivBattery.setColorFilter(getColor(R.color.status_offline))
    }
}
```

## 8. Bluetooth Command Helper

```kotlin
private fun sendBluetoothCommand(command: String) {
    if (!isConnected) {
        Toast.makeText(this, "Not connected to rover", Toast.LENGTH_SHORT).show()
        return
    }
    
    try {
        // Send command via Bluetooth
        // Your Bluetooth implementation here
        // Example: bluetoothSocket?.outputStream?.write(command.toByteArray())
        
        Log.d("SmartRover", "Sent command: $command")
    } catch (e: Exception) {
        Log.e("SmartRover", "Error sending command: ${e.message}")
        Toast.makeText(this, "Failed to send command", Toast.LENGTH_SHORT).show()
    }
}
```

## Summary of Commands

Here are the suggested Bluetooth commands for Arduino:

| Command | Description |
|---------|-------------|
| `F` | Forward |
| `B` | Backward |
| `L` | Left |
| `R` | Right |
| `S` | Stop |
| `M` | Manual Mode |
| `A` | Auto Mode |
| `H` | Horn |
| `W` | Lights ON |
| `w` | Lights OFF |
| `0-9` | Speed control (0 = slowest, 9 = fastest) |

## Additional Features to Implement

1. **Bluetooth Connection Management**: Implement proper Bluetooth pairing and connection handling
2. **Data Reception**: Listen for incoming data from Arduino (battery level, sensor readings, auto mode status)
3. **Landscape Orientation Lock**: Add to AndroidManifest.xml:
   ```xml
   <activity
       android:name=".HomeActivity"
       android:screenOrientation="landscape" />
   ```
4. **Keep Screen On**: Add in onCreate():
   ```kotlin
   window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
   ```

This implementation provides a complete, interactive control interface for your Smart Rover with all the visual enhancements and safety features requested.
