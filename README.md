
# 🐾 Wildlife Monitoring System for Development in Tourism

This project introduces a **solar-powered, real-time wildlife monitoring system** that enhances tourism by providing live animal detection, classification, and notifications through a mobile app. The system leverages edge computing, AI, and long-range wireless communication to deliver efficient and scalable wildlife monitoring in remote areas.

---

## 🎯 Project Objectives

- Boost eco-tourism by enabling real-time wildlife sightings.
- Enhance conservation through automated monitoring.
- Provide a user-friendly experience via a dedicated mobile application.
- Support wildlife-related research and law enforcement with data analytics.

---

## 🔧 Key Technologies

| Feature                    | Description                                                                       |
|----------------------------|-----------------------------------------------------------------------------------|
| **Animal Detection**       | Real-time CNN-based species classification on captured images                     |
| **Edge Computing**         | Raspberry Pi Zero2W perform local processing                                      |
| **Power Management**       | STM32 Microcontorller for Power Management                                        |
| **Power Source**           | Solar-powered units with rechargeable backup batteries                            |
| **Communication**          | Long-range LoRa WAN to central hub, Bluetooth for mobile device connection        |
| **Mobile App**             | Displays animal sightings, real-time alerts, navigation support                   |
| **Smart Algorithms**       | Optimizes vehicle paths for tourist flow, efficiency, and safety                  |
| **Database Integration**   | Centralized storage for research, anti-poaching, and wildlife conflict prevention |

---

## 📐 System Architecture

```
[Motion Sensor] ──> [STM32] ──> [Raspberry Pi] ──> CNN [Local Decision Making]
                                   ↓
                            LoRa Transmission
                                   ↓
                            [Central Hub] ──>[Database]
                                   ↓
                            [Vehical Module (Lora to Blutooth Interfacing)]
                                   ↓
                             [App Backend]
```

---

## 📲 Mobile Application Features

- 📸 Live animal detection alerts
- 📍 Wildlife location navigation
- 🐅 Animal type filter & sighting history
- 🌐 Connected via Bluetooth to nearby monitoring nodes or central hub

---

## 🌍 Real-World Impact

- **Tourism**: Improves visitor experience with real-time encounters.
- **Research**: Data logging enables pattern analysis and habitat studies.
- **Conservation**: Supports anti-poaching strategies and conflict mitigation.
- **Sustainability**: Operates off-grid using solar energy.

---

## 🗂️ Folder Structure

```
wildlife-monitoring-system/
├── firmware/         # STM32 and Pi firmware (CNN inference, sensor handling)
├── mobile-app/       # Android/iOS app codebase (React Native / Java / Kotlin)
├── pcb/              # Circuit schematics and board designs
├── docs/             # System diagrams, use-case flows, presentation material
├── models/           # CNN model files and training data references
└── README.md         # Project overview (this file)
```
---

## 👥 Authors & Acknowledgments

- **Thamara Banneheka**, **K.A.I.I.V. Abhayarathne**, **W.K.D.B. Wanniarachchi**  
  Department of Electrical and Electronic Engineering, SLIIT  
- Supervised by: *Shehani Jayasinghe, Dr. Eranga Wijesinghe, Dr. Nushara Wedasingha*