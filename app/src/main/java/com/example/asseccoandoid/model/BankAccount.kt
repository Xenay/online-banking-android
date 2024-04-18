package com.example.asseccoandoid.model

data class BankAccount(
    val recipientName: String,
    val name: String,
    val balance: Double,
    val type: String,
    val iban: String,
    val id: Long,
)
