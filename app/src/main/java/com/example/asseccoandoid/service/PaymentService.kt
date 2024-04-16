package com.example.asseccoandoid.service

import com.example.asseccoandoid.model.PaymentOrder
import com.example.asseccoandoid.response.PaymentResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentService {
    @POST("/api/payment-orders")
    fun createPaymentOrder(@Body paymentOrder: PaymentOrder): Call<PaymentResponse>
}