package com.example.zelinn.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zelinn.classes.NotificationModel

class NotificationViewModel: ViewModel() {
    private val _notifications = MutableLiveData<List<NotificationModel>>().apply {
        this.value = emptyList()
    }

    val notifications: LiveData<List<NotificationModel>> = _notifications

    fun setNotifications(notifications: List<NotificationModel>) {
        _notifications.value = notifications
    }
}