package com.example.asseccoandoid.service

import com.example.asseccoandoid.model.BankAccount
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BankAccountService {
    @GET("/api/user/bank-accounts")
    fun getBankAccounts(@Query("username") username: String): Call<List<BankAccount>>
}