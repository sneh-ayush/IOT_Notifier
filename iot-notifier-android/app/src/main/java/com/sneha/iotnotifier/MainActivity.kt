package com.sneha.iotnotifier

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.sneha.iotnotifier.databinding.ActivityMainBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = NotificationAdapter(emptyList())
        binding.recyclerViewNotifications.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotifications.adapter = adapter

        viewModel.notificationText.observe(this, Observer { text ->
            binding.textViewNotification.text = text
        })
        viewModel.notificationHistory.observe(this, Observer { history ->
            adapter.updateData(history)
        })

        viewModel.connectAndSubscribe(this)
        binding.buttonSubscribe.setOnClickListener {
            val tenant = binding.editTextTenant.text.toString()
            val client = binding.editTextClient.text.toString()
            viewModel.subscribe(this, tenant, client)
        }
    }
} 