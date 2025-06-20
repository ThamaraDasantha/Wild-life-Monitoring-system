import os
import numpy as np
import tflite_runtime.interpreter as tflite
from PIL import Image

# isuru start---------------------------------------------------------------------------------------------------------------
import serial
import smbus
from gpiozero import AngularServo
from gpiozero import LED
from time import sleep
import subprocess
from LoRaRF import SX127x, LoRaSpi, LoRaGpio
import time

# Load the TFLite model
MODEL_PATH = "Model/animal_model2.tflite"  # Change this to your actual model file path
interpreter = tflite.Interpreter(model_path=MODEL_PATH)
interpreter.allocate_tensors()

# Get input and output details
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

# Define class labels
LABELS = ["bear", "deer", "elephant", "leopard", "peacock"]

# Initialize LoRa transmitter
spi = LoRaSpi(0, 0)
cs = LoRaGpio(0, 8)
reset = LoRaGpio(0, 22)
LoRa = SX127x(spi, cs, reset)

if not LoRa.begin():
    raise Exception("Failed to initialize LoRa")

LoRa.setFrequency(433000000)
LoRa.setTxPower(17, LoRa.TX_POWER_PA_BOOST)
LoRa.setSpreadingFactor(7)
LoRa.setBandwidth(125000)
LoRa.setCodeRate(5)
LoRa.setHeaderType(LoRa.HEADER_EXPLICIT)
LoRa.setPreambleLength(12)
LoRa.setPayloadLength(50)
LoRa.setCrcEnable(True)
LoRa.setSyncWord(0x35)

def send_data_via_lora(animal_name, latitude, longitude):
    message = f"Animal: {animal_name}, Lat: {latitude}, Lon: {longitude}\0"
    message_list = [ord(c) for c in message]

    LoRa.beginPacket()
    LoRa.write(message_list, len(message_list))
    LoRa.endPacket()
    print(f"Transmitted: {message}")

# Configure the serial connection
ser = serial.Serial(
    port='/dev/ttyS0',
    baudrate=115200,
    parity=serial.PARITY_NONE,
    stopbits=serial.STOPBITS_ONE,
    bytesize=serial.EIGHTBITS,
    timeout=1
)

# SoC init start--------------
threshold=15.0  # Battery Low threshold in % 
address = 0x36  # I2C-Adddress of MAX17043

#registers
VCELL_REGISTER=0x02
SOC_REGISTER=0x04
MODE_REGISTER=0x06
VERSION_REGISTER=0x08
CONFIG_REGISTER=0x0C
COMMAND_REGISTER=0xFE

#  open the bus by creating an instance
MAX17043 = smbus.SMBus(1)

def reset():
    MAX17043.write_byte_data(address, COMMAND_REGISTER, 0x00)
    MAX17043.write_byte_data(address, COMMAND_REGISTER, 0x54)
 
def quickStart():
    MAX17043.write_byte_data(address, MODE_REGISTER, 0x40)
    MAX17043.write_byte_data(address, MODE_REGISTER, 0x00)

def getVersion():
    print (hex ( MAX17043.read_word_data(address, VERSION_REGISTER))) 

def getSOC():
    MSB= MAX17043.read_byte_data(address, SOC_REGISTER)
    LSB = MAX17043.read_byte_data(address, SOC_REGISTER)
    percentage= MSB+ LSB/256.0
    print(f"Battery SoC: {percentage}%")
    if percentage < threshold:
        print("Battery Low")
        #os.system("sudo shutdown -h now")

'''def getSOCarduino():
    #float percent;
    soc = MAX17043.read_word_data(address, SOC_REGISTER)
    percent = (float)((soc & 0xFF00) >> 8)
    percent += ((float)(soc & 0x00FF)) / 256.0

    print(f"Battery SoC: {percent}%")
''' 
 
def getVolt():
    vCell = MAX17043.read_word_data(address, VCELL_REGISTER)
    vCell = (vCell) >> 4
    divider = 4096.0 / 5.12
    voltage = (vCell) / divider
    print(f"Battery voltage: {voltage}V")

# SoC init end----------------
    
# Initialize the servo on GPIO17
# min_angle and max_angle parameters set the range of motion
# min_pulse_width and max_pulse_width might need adjustment for the specific servo
servo = AngularServo(17, 
                     min_angle=0, 
                     max_angle=180,
                     min_pulse_width=0.0005,  # 0.5ms
                     max_pulse_width=0.0025)  # 2.5ms

#servo reset
servo.angle = 90
sleep(0.5)
servo.detach()

#SoC reset
reset()
quickStart()
#getVersion()

#RPi status
RPi_status = LED(24)
RPi_status.on()

# Create a directory for the photos if it doesn't exist
output_dir = 'photos'
os.makedirs(output_dir, exist_ok=True)

def take_photo(angle):
    # Construct the filename based on the angle
    filename = os.path.join(output_dir, f'photo_animal.jpg')
    # Capture the image using libcamera-still
    subprocess.run(['libcamera-still', '-o', filename])
    print(f'Took photo at {angle} degrees')

def turn_servo(PIR):
	if PIR > 0:
		SerAngle = (PIR - 1)*90
		servo.angle = SerAngle
		sleep(0.5)
		servo.detach()
		sleep(0.5)
		take_photo(SerAngle)
	else:
		servo.angle = 0
		sleep(0.5)
		servo.detach()
		print("Invalid PIR")

#print(f"Waiting for UART data")
#ser.write(b'\x01')
         
# isuru end ----------------------------------------------------------------------------------------------------------------------------        


def preprocess_image(image_path, input_shape):
    """Prepares the image for model inference."""
    image = Image.open(image_path).convert("RGB")  # Convert to RGB (if not already)
    image = image.resize((input_shape[1], input_shape[2]))  # Resize to model's input size
    image = np.array(image, dtype=np.float32) / 255.0  # Normalize pixel values to [0,1]
    image = np.expand_dims(image, axis=0)  # Add batch dimension
    return image

def classify_image(image_path):
    """Classifies an image using the TFLite model."""
    input_shape = input_details[0]['shape']
    image_data = preprocess_image(image_path, input_shape)

    # Set the input tensor
    interpreter.set_tensor(input_details[0]['index'], image_data)

    # Run inference
    interpreter.invoke()

    # Get the output tensor
    output_data = interpreter.get_tensor(output_details[0]['index'])[0]
    predicted_index = np.argmax(output_data)
    predicted_label = LABELS[predicted_index]
    confidence = output_data[predicted_index]

    print(f"Animal type: {predicted_label} ({confidence:.2f})")
    return predicted_label, confidence


def get_location():
    # Dummy location, replace with GPS module integration if available
    latitude = 7.915590  # Example latitude
    longitude = 80.973904 # Example longitude
    return latitude, longitude

#ser.write(b'\x04') #to reset the stm32 when debugging, comment otherwise

# Main function
while True:
    # Continuously read and process the data from the STM32
    #while ser.in_waiting <= 0:
    print(f"Waiting for PIR")
    RPi_status.on()
    ser.write(b'\x01')
    
    #sleep(1)
    
    if ser.in_waiting > 0:
        detected_pir = ser.read()[0]
        print(f"Detected PIR: {detected_pir}")
        sleep(0.5)
        
        turn_servo(detected_pir)
        take_photo((detected_pir - 1)*90)
        
        
        image_path = "/home/tham/Documents/CDP/photos/photo_animal.jpg"  
        animal_name = classify_image(image_path)
        

        latitude, longitude = get_location()
        print(f"Latitude: {latitude}, Longitude: {longitude}")

        # Transmit Data via LoRa
        message = f"{animal_name},{latitude:.4f},{longitude:.4f}"
        message_bytes = list(message.encode())
        print(f"Transmitting: {message}")

        LoRa.beginPacket()
        LoRa.write(message_bytes, len(message_bytes))
        LoRa.endPacket()
        LoRa.wait()
        print("Data transmitted successfully")
    
        getSOC()
        #getVolt()
        sleep(10)  # Small delay before shutdown
        
        print("Shuting down")
        #servo reset
        servo.angle = 90
        sleep(0.5)
        servo.detach()
        RPi_status.off()
        #os.system("sudo shutdown now")    
    sleep(1)

#if __name__ == "__main__":
#    main()
