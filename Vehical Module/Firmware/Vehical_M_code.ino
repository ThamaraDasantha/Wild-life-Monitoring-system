#include "BluetoothSerial.h"
#include "LoRa.h"

#define ledpin1 12  // LED 1 for Bluetooth connectivity
#define ledpin2 13  // LED 2 for LoRa sending debug
#define ledpin3 14  // LED 3 for LoRa receiving debug

// Create BluetoothSerial object
BluetoothSerial SerialBT;

// Variable to check if Bluetooth is connected
bool isConnected = false;

// LoRa configurations
#define LORA_SS 2  // LoRa chip select
#define LORA_RST 4  // LoRa reset
#define LORA_DIO0 26 // LoRa DIO0

void setup() {
  // Initialize Serial Monitor
  Serial.begin(115200);

  // Initialize Bluetooth Serial 
  SerialBT.begin("Trailguide");  
  Serial.println("Bluetooth device is ready to pair");

  // Register Bluetooth callback
  SerialBT.register_callback(btCallback);

  // Initialize LED pins
  pinMode(ledpin1, OUTPUT);
  pinMode(ledpin2, OUTPUT);
  pinMode(ledpin3, OUTPUT);

  // Initialize LoRa module
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(915E6)) { // Set LoRa frequency (915 MHz here)
    Serial.println("Starting LoRa failed!");
    digitalWrite(ledpin3, HIGH);
    while (1);
  }
  Serial.println("LoRa initialized");
  digitalWrite(ledpin2, HIGH);
}

void loop() {
  // Check if Bluetooth is connected
  if (isConnected) {
    // Read data from Bluetooth
    if (SerialBT.available()) {
      String incomingData = SerialBT.readString();
      Serial.print("Received from Bluetooth: ");
      Serial.println(incomingData);

      // Send the Bluetooth message over LoRa
      sendLoRaMessage(incomingData);
    }
    
    // Send sample data via Bluetooth and LoRa
    sampledata();

  } else {
    Serial.println("Waiting for Bluetooth connection...");
    ledblink1();
  }

  // Check if there's incoming data from LoRa
  if (LoRa.parsePacket()) {
    String receivedLoRaMessage = "";
    while (LoRa.available()) {
      receivedLoRaMessage += (char)LoRa.read();
    }
    Serial.print("Received from LoRa: ");
    Serial.println(receivedLoRaMessage);

    // Blink LED 3 to indicate LoRa reception
    ledblink3();

    // Send the LoRa message to Bluetooth
    if (isConnected) {
      SerialBT.println(receivedLoRaMessage);
    }
  }
}

// Callback function to handle Bluetooth connection events
void btCallback(esp_spp_cb_event_t event, esp_spp_cb_param_t *param) {
  if (event == ESP_SPP_SRV_OPEN_EVT) {
    Serial.println("Bluetooth connected!");
    digitalWrite(ledpin1, HIGH);
    isConnected = true;
  } else if (event == ESP_SPP_CLOSE_EVT) {
    Serial.println("Bluetooth disconnected!");
    digitalWrite(ledpin1, LOW);
    isConnected = false;
  }
}

void sampledata() {
  // Sample data to send (can be dynamic or based on real inputs)
  String animalType = "Bear";
  float latitude = 6.927079;
  float longitude = 79.861244;

  // Format the message to send in the required format
  String message = animalType + "," + String(latitude, 6) + "," + String(longitude, 6);

  // Send the message via Bluetooth
  SerialBT.println(message);
  Serial.println("Message sent to Bluetooth");

  // Send the same message via LoRa
  sendLoRaMessage(message);

  delay(10000); // Send the message every 10 seconds for testing
}

// Function to send messages via LoRa
void sendLoRaMessage(String message) {
  LoRa.beginPacket();
  LoRa.print(message);
  LoRa.endPacket();
  Serial.println("Message sent to LoRa");

  // Blink LED 2 to indicate LoRa sending
  ledblink2();
}

void ledblink1() {  
  digitalWrite(ledpin1, HIGH);
  delay(500); 
  digitalWrite(ledpin1, LOW);
  delay(500); 
}

void ledblink2() {
  digitalWrite(ledpin2, HIGH);
  delay(100); 
  digitalWrite(ledpin2, LOW);
}

void ledblink3() {
  digitalWrite(ledpin3, HIGH);
  delay(100); 
  digitalWrite(ledpin3, LOW);
}
