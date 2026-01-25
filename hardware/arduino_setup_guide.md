# 🛠️ Arduino Setup Guide

Follow these steps to prepare your Arduino Uno board for the Smart Rover.

## 1. Software Requirements
- Download and install the **Arduino IDE** (v2.0 or later recommended).
- No external libraries are strictly required for this sketch (SoftwareSerial and Servo are built-in).

## 2. Flashing the Firmware
1. Open the Arduino IDE.
2. Go to `File` -> `Open` and select [SmartRover.ino](file:///e:/SmartRover/hardware/SmartRover.ino).
3. Connect your Arduino Uno to your computer via USB.
4. Go to `Tools` -> `Board` and select **Arduino Uno**.
5. Go to `Tools` -> `Port` and select the COM port your board is connected to.
6. **IMPORTANT**: If your Bluetooth module is connected to pins 0 and 1 (Hardware Serial), disconnect it before uploading. Since we are using pins 2 and 3 (SoftwareSerial), you don't need to disconnect it.
7. Click the **Upload** button (Arrow icon).

## 3. Hardware Assembly Tips
- **Servo & Sensor**: Mount the Ultrasonic sensor on top of the Servo motor using double-sided tape or a 3D-printed bracket.
- **Motor Polarization**: If a motor spins the wrong way, just swap its two wires on the L298N terminal blocks.
- **Bluetooth Placement**: Place the HC-05/06 module away from high-power wires to reduce interference.

## 4. Troubleshooting
- **Car doesn't move**: Check if the L298N "EN" jumpers are removed and replaced with Arduino PWM pins (D5, D6).
- **Bluetooth won't pair**: Ensure the module's LED is blinking fast. Default PIN is usually `1234` or `0000`.
- **Sensors act weird**: Ensure all components share a **Common Ground** (GND).
