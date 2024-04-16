package com.example.asseccoandoid.model

data class UserInfo(
    val username: String,
    val password: String
)

data class UserResponse(
    val username: String,
    val userId: Int,
    val isAuthenticated: Boolean
)