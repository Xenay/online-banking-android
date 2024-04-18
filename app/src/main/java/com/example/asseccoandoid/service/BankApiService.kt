package com.example.asseccoandoid.service

import com.example.asseccoandoid.model.Transaction
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BankApiService {
    @GET("api/transactions/user")
    fun getUserTransactions(@Query("accountId") accountId: Long): Call<List<Transaction>>
}
