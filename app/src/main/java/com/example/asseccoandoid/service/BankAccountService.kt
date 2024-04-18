package com.example.asseccoandoid.service

import com.example.asseccoandoid.model.BankAccount
import com.example.asseccoandoid.model.TransferPayload
import com.example.asseccoandoid.response.TransferResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BankAccountService {
    @GET("/api/user/bank-accounts")
    fun getBankAccounts(@Query("username") username: String): Call<List<BankAccount>>
    @POST("/api/bank-accounts/transfer")
    fun transfer(@Body payload: TransferPayload): Call<TransferResponse>
}