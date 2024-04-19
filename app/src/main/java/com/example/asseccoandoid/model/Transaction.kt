package com.example.asseccoandoid.model

data class Transaction(
    val id: Long,
    val recipientName: String?,
    val senderIban: String,
    val amount: Double,
    val transactionDate: String,
    val paymentDescription: String?,
    var accountId: Long,
    val recipientIban: String?,
    val paymentTypeId: Long,
    val paymentType: String,
    val account: AccountFetch
)
data class AccountFetch(
    val id: Long,
    val accountNumber: String,
    val username: String
)


