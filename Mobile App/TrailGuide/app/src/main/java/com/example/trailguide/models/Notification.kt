package com.example.trailguide.models

data class Notification(
    val animalType: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String // Adding timestamp field
)
