package com.example.asseccoandoid


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.example.asseccoandoid.R
import com.example.asseccoandoid.model.UserInfo
import com.example.asseccoandoid.model.UserResponse
import com.example.asseccoandoid.service.LoginResponse
import com.example.asseccoandoid.service.LoginService
import com.example.asseccoandoid.singleton.RetrofitClient
import com.example.asseccoandoid.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var languageSpinner: Spinner
    private lateinit var sessionManager: SessionManager  // Session manager to manage login session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        sessionManager = SessionManager(this)  // Initialize the session manager

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        languageSpinner = findViewById(R.id.languageSpinner)

        setupLanguageSpinner()
        loginButton.setOnClickListener { handleLogin() }
        registerButton.setOnClickListener { handleRegister() }
    }

    private fun setupLanguageSpinner() {
        val languages = arrayOf("Hrvatski", "English")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        languageSpinner.adapter = adapter
    }

    private fun handleLogin() {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        Log.d("LoginActivity", "Attempting login with username: $username")

        val authService = RetrofitClient.createService(LoginService::class.java)
        val loginCall = authService.loginUser(UserInfo(username, password))

        loginCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse != null && userResponse.isAuthenticated) {
                        Log.d("LoginActivity", "Login success: User is authenticated")

                        // Use the actual user ID from the login response
                        val userId = userResponse.userId // Convert userId to String if your session manager expects a String
                        sessionManager.createLoginSession(username, userId)

                        navigateToDashboard()
                    } else {
                        Log.d("LoginActivity", "Login failed: User is not authenticated")
                    }
                } else {
                    Log.d("LoginActivity", "HTTP error response: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Login request failed: ${t.message}", t)
            }
        })
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleRegister() {
        // Navigate to the Registration Activity
        // This can be implemented as needed
    }
}


