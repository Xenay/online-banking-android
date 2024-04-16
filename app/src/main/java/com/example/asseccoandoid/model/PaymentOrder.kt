package com.example.asseccoandoid.model

data class PaymentOrder(
    val recipientName: String,
    val senderIban: String,
    val recipientIban: String,
    val amount: Double,
    val paymentDescription: String,
    val accountId: Long,  // Changed from String to Long
    val paymentType: String
)
