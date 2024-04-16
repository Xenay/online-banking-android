package com.example.asseccoandoid.service


import com.example.asseccoandoid.model.UserInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/login")
    fun loginUser(@Body loginData: UserInfo): Call<LoginResponse>
}

data class LoginData(val username: String, val password: String)
data class LoginResponse(val username: String, val userId: Long, val isAuthenticated: Boolean)
