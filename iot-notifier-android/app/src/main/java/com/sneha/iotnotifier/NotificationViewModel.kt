package com.sneha.iotnotifier

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val _notificationText = MutableLiveData<String>("Waiting for notifications...")
    val notificationText: LiveData<String> = _notificationText

    private val _notificationHistory = MutableLiveData<List<String>>(emptyList())
    val notificationHistory: LiveData<List<String>> = _notificationHistory

    private val mqttRepository = MqttRepository { message ->
        _notificationText.postValue(message)
        _notificationHistory.postValue(_notificationHistory.value.orEmpty() + message)
    }

    fun connectAndSubscribe(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            mqttRepository.connectAndSubscribe(context)
        }
    }

    fun subscribe(context: android.content.Context, tenantPrefix: String?, clientIdInput: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            mqttRepository.connectAndSubscribe(context, tenantPrefix, clientIdInput)
        }
    }
} 