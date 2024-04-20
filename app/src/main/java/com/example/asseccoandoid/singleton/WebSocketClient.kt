package com.example.asseccoandoid.singleton

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.asseccoandoid.R
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.UUID


class WebSocketClient(private val context: Context) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val SERVER_PATH = "ws://10.0.2.2:9090/ws"

    init {
        val request = Request.Builder().url(SERVER_PATH).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.e("com.example.asseccoandoid.singleton.WebSocketClient", "Connection opened")
                sendStompConnectFrame()
                subscribeToTopic("/topic/notifications")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.e("com.example.asseccoandoid.singleton.WebSocketClient", "Message received: $text")
                if (text.contains("Payment Submitted")) {  // Check if the message contains "Payment Submitted"
                    showNotification("Payment Success", "Your payment was successfully submitted.")
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.e("com.example.asseccoandoid.singleton.WebSocketClient", "Message received: $bytes")

            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                Log.e("com.example.asseccoandoid.singleton.WebSocketClient", "Connection closing")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("com.example.asseccoandoid.singleton.WebSocketClient", "Connection failed: ${t.message}, response: $response")
            }
        })
    }

    private fun sendStompConnectFrame() {
        val connectFrame = "CONNECT\naccept-version:1.1,1.0\nhost:10.0.2.2\n\n\u0000"
        sendMessage(connectFrame)
        // Typically you would also send a SUBSCRIBE frame here
    }

    private fun subscribeToTopic(topic: String) {
        val subscriptionId = UUID.randomUUID().toString()
        val subscribeFrame = "SUBSCRIBE\nid:$subscriptionId\ndestination:$topic\n\n\u0000"
        sendMessage(subscribeFrame)
        Log.d("com.example.asseccoandoid.singleton.WebSocketClient", "Subscribe Frame: $subscribeFrame")
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
        Log.d("com.example.asseccoandoid.singleton.WebSocketClient", "Message sent: $message")
    }
    fun sendStompSendFrame(destination: String, payload: String) {
        val sendFrame = "SEND\ndestination:$destination\n\n$payload\u0000"
        sendMessage(sendFrame)
    }

    fun closeConnection() {
        val disconnectFrame = "DISCONNECT\n\n\u0000"
        sendMessage(disconnectFrame)
        webSocket?.close(1000, "Closing connection")
    }

    private fun showNotification(title: String, content: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("PaymentNotifications", "Payment Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "PaymentNotifications")
            .setSmallIcon(R.drawable.logo_high_res)  // replace with your app icon
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, builder.build())
    }

}
