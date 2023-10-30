
# Documentation


## Introduction:

    The Mini Fire Extinguisher Vehicle with Robotic Arm is a portable firefighting solution equipped with a robotic arm controlled through a mobile Android app.
    The vehicle utilizes an ESP32 microcontroller and Wi-Fi communication to interact with the Android app.
    The app, developed in Java, employs Volley library for seamless communication with ESP32, enabling control over the vehicle's movements, pump operation, and robotic arm.




## Components:

    ESP32 Microcontroller: This acts as the brain of the vehicle, managing Wi-Fi communication and controlling the motors and pump.
    Robotic Arm: Comprising servo motors, the arm is responsible for precise movements and firefighting operations.
    Pump: The water pump is used for extinguishing fires.
    Android App: Developed in Java, this app allows users to control the vehicle and arm via Wi-Fi.




## Communication:

    The Android app communicates with the ESP32 microcontroller through Wi-Fi using the Volley library.
    Commands from the app are sent to the ESP32, which interprets these commands to control the motors and pump accordingly.




## Files:

    embedded_program.ino: This file contains the Arduino code for the ESP32 microcontroller, responsible for motor control, pump operation, and interpreting commands from the app.
    MainActivity.java: This file contains the main logic for the Android app. It establishes communication with the ESP32 using Volley, sends commands based on user input, and manages the app interface. It is the main part of the Android Studio project.
    APK File: This is the Android app installation file that users can install on their devices to control the mini fire extinguisher vehicle.




## Setup Instructions:

    Upload Embedded Code: Upload the embedded_program.ino file to your ESP32 microcontroller using the Arduino IDE.
    Install App: Install the provided APK file on your Android device.
    Connect to Wi-Fi: Connect both the ESP32 and your Android device to the same Wi-Fi network.
    Launch App: Launch the Android app and establish a connection with the ESP32 by entering the appropriate IP address and port number.
    Control the Vehicle: Use the intuitive interface in the app to control the vehicle's movements, pump operation, and robotic arm.




## App Features:

    Vehicle Control: The app allows users to move the vehicle forward, backward, left, and right. The speed of the vehicle is also controlled via a slider.
    Pump Control: Users can control the intensity of the water pump for firefighting operations.
    Robotic Arm Control: Precise control of the robotic arm's movements, including rotation and extension, is facilitated through the app.
