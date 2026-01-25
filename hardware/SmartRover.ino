/*
 * Smart Rover - Arduino Firmware
 * Controls: Manual (Bluetooth) & Autonomous (Obstacle Avoidance)
 * Hardware: Arduino Uno, L298N, HC-SR04, HC-05/06, Servo, Buzzer
 */

#include <SoftwareSerial.h>
#include <Servo.h>

// --- Pin Definitions ---
SoftwareSerial BTSerial(2, 3); // RX (to TX of HC-05), TX (to RX of HC-05)

const int ENA = 5;  // PWM
const int IN1 = 7;
const int IN2 = 8;
const int IN3 = 9;
const int IN4 = 10;
const int ENB = 6;  // PWM

const int TRIG = 12;
const int ECHO = 11;
const int SERVO_PIN = 4;
const int BUZZER = 13;

// --- Global Variables ---
Servo myServo;
char command;
int motorSpeed = 150; 
bool isAutoMode = false;
int distance = 0;

void setup() {
  BTSerial.begin(9600);
  Serial.begin(9600);
  
  pinMode(ENA, OUTPUT);
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);
  pinMode(ENB, OUTPUT);
  pinMode(BUZZER, OUTPUT);
  
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);
  
  myServo.attach(SERVO_PIN);
  myServo.write(90); 
  
  stopCar();
  Serial.println("Smart Rover Ready!");
}

void loop() {
  if (BTSerial.available()) {
    command = BTSerial.read();
    handleCommand(command);
  }

  if (isAutoMode) {
    autoDriveLogic();
  }
}

void handleCommand(char cmd) {
  if (cmd == 'A') { isAutoMode = true; return; }
  if (cmd == 'M') { isAutoMode = false; stopCar(); return; }
  
  if (cmd >= '0' && cmd <= '9') {
    motorSpeed = map(cmd - '0', 0, 9, 0, 255);
    return;
  }

  if (!isAutoMode) {
    switch (cmd) {
      case 'F': driveForward(); break;
      case 'B': driveBackward(); break;
      case 'L': turnLeft(); break;
      case 'R': turnRight(); break;
      case 'S': stopCar(); break;
      case 'H': horn(); break;
    }
  }
}

void driveForward() {
  analogWrite(ENA, motorSpeed);
  analogWrite(ENB, motorSpeed);
  digitalWrite(IN1, HIGH); digitalWrite(IN2, LOW);
  digitalWrite(IN3, HIGH); digitalWrite(IN4, LOW);
}

void driveBackward() {
  analogWrite(ENA, motorSpeed);
  analogWrite(ENB, motorSpeed);
  digitalWrite(IN1, LOW); digitalWrite(IN2, HIGH);
  digitalWrite(IN3, LOW); digitalWrite(IN4, HIGH);
}

void turnLeft() {
  analogWrite(ENA, motorSpeed);
  analogWrite(ENB, motorSpeed);
  digitalWrite(IN1, LOW); digitalWrite(IN2, HIGH);
  digitalWrite(IN3, HIGH); digitalWrite(IN4, LOW);
}

void turnRight() {
  analogWrite(ENA, motorSpeed);
  analogWrite(ENB, motorSpeed);
  digitalWrite(IN1, HIGH); digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW); digitalWrite(IN4, HIGH);
}

void stopCar() {
  digitalWrite(IN1, LOW); digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW); digitalWrite(IN4, LOW);
}

void horn() {
  digitalWrite(BUZZER, HIGH);
  delay(200);
  digitalWrite(BUZZER, LOW);
}

void autoDriveLogic() {
  distance = getDistance();
  if (distance > 30) {
    driveForward();
  } else {
    stopCar();
    int leftDist = scanDirection(180);
    delay(300);
    int rightDist = scanDirection(0);
    delay(300);
    myServo.write(90);
    if (leftDist > rightDist) {
      turnLeft(); delay(500);
    } else {
      turnRight(); delay(500);
    }
    stopCar();
  }
}

int getDistance() {
  digitalWrite(TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG, LOW);
  long duration = pulseIn(ECHO, HIGH);
  return duration * 0.034 / 2;
}

int scanDirection(int angle) {
  myServo.write(angle);
  delay(500);
  return getDistance();
}
