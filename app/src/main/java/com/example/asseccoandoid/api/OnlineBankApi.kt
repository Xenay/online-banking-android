package com.example.asseccoandoid.api

import android.accounts.Account
import retrofit2.Call
import retrofit2.http.GET

interface OnlineBankApi {
    @GET("api/account")
    fun getAccountDetails(): Call<Account>
}