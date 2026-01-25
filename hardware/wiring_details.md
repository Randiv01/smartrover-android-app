# 🔌 Smart Rover: Wiring & Pin Details

Connect your components to the **Arduino Uno** exactly as listed below.

## 1. Bluetooth Module (HC-05 / HC-06)
| HC-05/06 Pin | Arduino Pin | Notes |
| :--- | :--- | :--- |
| **VCC** | 5V | Power |
| **GND** | GND | Ground |
| **TXD** | D2 | SoftwareSerial RX |
| **RXD** | D3 | SoftwareSerial TX (Use 1k/2k divider) |

## 2. Motor Driver (L298N)
| L298N Pin | Arduino Pin | Notes |
| :--- | :--- | :--- |
| **ENA** | D5 | Left Motors PWM (Speed) |
| **IN1** | D7 | Left Forward |
| **IN2** | D8 | Left Backward |
| **IN3** | D9 | Right Forward |
| **IN4** | D10 | Right Backward |
| **ENB** | D6 | Right Motors PWM (Speed) |
| **12V In** | Battery (+) | From battery pack |
| **GND** | Battery (-) | **Link to Arduino GND** |
| **5V Out** | Arduino 5V | Optional: Power Arduino from driver |

## 3. Ultrasonic Sensor (HC-SR04)
| HC-SR04 Pin | Arduino Pin | Notes |
| :--- | :--- | :--- |
| **VCC** | 5V | |
| **Trig** | D12 | |
| **Echo** | D11 | |
| **GND** | GND | |

## 4. Servo Motor (Sg90)
| Servo Wire | Arduino Pin | Notes |
| :--- | :--- | :--- |
| **Orange (Signal)** | D4 | PWM Pin |
| **Red (VCC)** | 5V | |
| **Brown (GND)** | GND | |

## 5. Buzzer (Horn)
| Buzzer Pin | Arduino Pin | Notes |
| :--- | :--- | :--- |
| **(+) Positive** | D13 | |
| **(-) Negative** | GND | |

---

## ⚡ Power Supply Setup
- **Batteries**: Use 4x 18650 batteries in series (approx 14.8V - 16.8V).
- **Wiring**:
  - Connect Battery (+) to L298N **12V** terminal.
  - Connect Battery (-) to L298N **GND** terminal.
  - **IMPORTANT**: Connect a wire from L298N **GND** to Arduino **GND**.
