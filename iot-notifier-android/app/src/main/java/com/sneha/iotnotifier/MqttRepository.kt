package com.sneha.iotnotifier

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttRepository(private val onMessageReceived: (String) -> Unit) {
    private var mqttClient: MqttAndroidClient? = null
    private val serverUri = "tcp://broker.hivemq.com:1883" // Replace with your broker
    private var topic: String = "iotnotifier/all" // Default, will be set dynamically
    private val clientId = "AndroidClient_" + System.currentTimeMillis()

    fun connectAndSubscribe(context: Context? = null, tenantPrefix: String? = null, clientIdInput: String? = null) {
        if (context == null) return
        topic = if (!tenantPrefix.isNullOrBlank()) {
            if (!clientIdInput.isNullOrBlank()) {
                "${tenantPrefix}/client/${clientIdInput}"
            } else {
                "${tenantPrefix}/all"
            }
        } else {
            "iotnotifier/all"
        }
        mqttClient = MqttAndroidClient(context, serverUri, clientId)
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true
        options.isCleanSession = true
        try {
            mqttClient?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    subscribeToTopic()
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e("MQTT", "Connection failed: ${exception?.message}")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun subscribeToTopic() {
        mqttClient?.subscribe(topic, 1, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("MQTT", "Subscribed to $topic")
            }
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e("MQTT", "Subscribe failed: ${exception?.message}")
            }
        })
        mqttClient?.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.e("MQTT", "Connection lost: ${cause?.message}")
            }
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                onMessageReceived(message?.toString() ?: "")
            }
            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })
    }
} 