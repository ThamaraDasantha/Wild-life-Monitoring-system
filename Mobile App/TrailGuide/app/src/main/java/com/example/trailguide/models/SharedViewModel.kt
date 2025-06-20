package com.example.trailguide.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trailguide.models.Notification

class SharedViewModel : ViewModel() {

    private val _mapNotifications = MutableLiveData<List<Notification>>()
    val mapNotifications: LiveData<List<Notification>> get() = _mapNotifications

    fun updateMapNotifications(notifications: List<Notification>) {
        _mapNotifications.value = notifications
    }
}
