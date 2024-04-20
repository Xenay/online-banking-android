package com.example.asseccoandoid.listeners

import android.content.Context
import com.example.asseccoandoid.singleton.WebSocketClient
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext

interface PaymentDialogListener {

    fun onPaymentSuccess(context: Context) {

        Log.d("PaymentDialogListener", "IN THE LISTENER")
        val webSocketClient = WebSocketClient(context) // Initialize WebSocketClient
        // Assuming you want to send this to a specific STOMP destination:
        val destination = "/topic/notifications"
        val payload = "Payment Submitted"  // You might want to send more structured data, like JSON
        webSocketClient.sendStompSendFrame(destination, payload)
    }


}