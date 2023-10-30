#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiAP.h>
#include <ESP32_Servo.h>
#include "NewPing.h"

Servo myservo_base;
Servo myservo_elbow;

#define enA 16
#define in1 19
#define in2 18

#define in3 5
#define in4 17

#define TRIGGER_PIN_F 21
#define ECHO_PIN_F 21
#define TRIGGER_PIN_B 22
#define ECHO_PIN_B 22
#define MAX_DISTANCE 500

int motorSpeedA = 0;

int pump = 23;

int pump_pressure, car_speed;
int current_base_angle, current_elbow_angle;

bool forward_pressed, reverse_pressed, left_pressed, right_pressed;
int threshold = 20;
int w_threshold = 50;


const char* ssid = "SAFBOT";
const char* password = "123456789";

NewPing sonar(TRIGGER_PIN_F, ECHO_PIN_F, MAX_DISTANCE);
WiFiServer server(80);

int* extractParameterValues(const String& input);
void moveForward();
void reverse();
void turnLeft();
void turnRight();
void halt();

void setup() {
  Serial.begin(115200);
  Serial.println();
  Serial.println("Configuring access point...");

  if (!WiFi.softAP(ssid, password))
  {
    log_e("Soft AP creation failed.");
    while (1);
  }

  IPAddress myIP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(myIP);
  server.begin();

  Serial.println("Server started");

  myservo_base.attach(2);
  myservo_elbow.attach(0);

  pinMode(pump, OUTPUT);
  pinMode(enA, OUTPUT);
  pinMode(in1, OUTPUT);
  pinMode(in2, OUTPUT);
  pinMode(in3, OUTPUT);
  pinMode(in4, OUTPUT);
}

void loop() {
  WiFiClient client = server.available();  // listen for incoming clients

  if (client)
  {
    String receivedData = "";
    bool requestComplete = false;
    Serial.println("Data Received");

    while (client.connected())
    {
      if (client.available())
      {
        char c = client.read();
        receivedData += c;
        Serial.print(c);

        // Check if the request is complete (end of headers)
        if (receivedData.endsWith("\r\n\r\n"))
        {
          requestComplete = true;
          break;
        }
      }
    }

    if (requestComplete)
    {
      int* parameters_array = extractParameterValues(receivedData);

      current_base_angle = parameters_array[0];
      car_speed = parameters_array[1];
      reverse_pressed = parameters_array[2];
      pump_pressure = parameters_array[3];
      forward_pressed = parameters_array[4];
      current_elbow_angle = parameters_array[5];
      left_pressed = parameters_array[6];
      right_pressed = parameters_array[7];

      // Free the dynamically allocated memory
      free(parameters_array);

      int val_base;                                        
      val_base = map(current_base_angle, 0, 255, 0, 180);  
      myservo_base.write(val_base);                        
                                                          
      int val_elbow;                                       
      val_elbow = map(current_elbow_angle, 0, 255, 0, 180);
      myservo_elbow.write(val_elbow);
                                                            
      int speed1 = map(car_speed, 0, 1023, 0, 255);


      int distance_f = sonar.ping_cm();


      analogWrite(enA, speed1);

      if (forward_pressed)
      {
        if (distance_f <= threshold) analogWrite(enA, 0);
        moveForward();
      }
      else if (reverse_pressed)
      {
        reverse();
      }
      else if (left_pressed)
      {
        analogWrite(enA, 255);
        turnLeft();
      }
      else if (right_pressed)
      {
        analogWrite(enA, 255);
        turnRight();
      }
      else
      {
        halt();
      }


      int pump_speed = map(pump_pressure, 0, 1023, 0, 255);
      analogWrite(pump, pump_speed);

      Serial.print("BASE: ");
      Serial.println(current_base_angle);
      Serial.print("CAR: ");
      Serial.println(car_speed);
      Serial.print("BACK: ");
      Serial.println(reverse_pressed);
      Serial.print("PUMP: ");
      Serial.println(pump_pressure);
      Serial.print("FRONT: ");
      Serial.println(forward_pressed);
      Serial.print("ELBOW: ");
      Serial.println(current_elbow_angle);

      // Send an HTTP response
      client.println("HTTP/1.1 200 OK");
      client.println("Content-Type: text/plain");
      client.println();
      client.println("I RECEIVED YOUR REQUEST!!!");
      client.println();

      // close the connection:
      client.stop();
      Serial.println("THIS IS MY RESPONSE!!!.");
    }
    else
    {
      // Request is incomplete or timed out, close the connection:
      client.stop();
      Serial.println("Incomplete Request, Connection Closed");
    }
  }
}

int* extractParameterValues(const String& input)
{
  int* paramsArray = new int[8];

  int lastLineStart = input.lastIndexOf('?') + 1;
  String lastLine = input.substring(lastLineStart);

  int paramsStart = 0;
  String params = lastLine.substring(paramsStart);

  int paramIndex = 0;
  int startPos = 0;
  while (startPos < params.length() && paramIndex < 8)
  {
    int separatorPos = params.indexOf('&', startPos);
    if (separatorPos == -1) {
      separatorPos = params.length();
    }

    String paramPair = params.substring(startPos, separatorPos);
    int equalPos = paramPair.indexOf('=');
    if (equalPos != -1) {
      String valueString = paramPair.substring(equalPos + 1);
      paramsArray[paramIndex] = valueString.toInt();
      paramIndex++;
    }

    startPos = separatorPos + 1;
  }

  return paramsArray;
}


void moveForward()
{
  digitalWrite(in1, HIGH); 
  digitalWrite(in2, LOW);

  digitalWrite(in3, HIGH); 
  digitalWrite(in4, LOW);
}

void reverse()
{
  digitalWrite(in1, LOW);  
  digitalWrite(in2, HIGH);

  digitalWrite(in3, LOW);  
  digitalWrite(in4, HIGH);
}

void turnLeft()
{
  digitalWrite(in1, LOW);
  digitalWrite(in2, HIGH);

  digitalWrite(in3, HIGH);
  digitalWrite(in4, LOW);
}

void turnRight()
{
  digitalWrite(in1, HIGH);
  digitalWrite(in2, LOW);

  digitalWrite(in3, LOW);
  digitalWrite(in4, HIGH);
}

void halt()
{
  digitalWrite(in1, LOW);  
  digitalWrite(in2, LOW);

  digitalWrite(in3, LOW);  
  digitalWrite(in4, LOW);
}