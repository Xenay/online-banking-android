

package com.example.asseccoandoid.model

data class TransferPayload(
    val fromAccountId: Long,
    val toAccountId: Long,
    val amount: Double,
    val description: String
)
