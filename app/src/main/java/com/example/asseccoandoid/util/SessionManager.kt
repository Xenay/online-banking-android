package com.example.asseccoandoid.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    companion object {
        private const val PREF_NAME = "UserSession"
        private const val IS_LOGIN = "IsLoggedIn"
        const val KEY_USERNAME = "Username"
        const val KEY_USER_ID = "UserId"
    }

    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    /**
     * Create login session
     */
    fun createLoginSession(username: String, userId: Long) {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_USERNAME, username)
        editor.putLong(KEY_USER_ID, userId)
        editor.apply()
    }

    /**
     * Check login status
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGIN, false)
    }

    /**
     * Get stored session data
     */
    fun getUserDetails(): HashMap<String, String> {
        val user = HashMap<String, String>()
        user[KEY_USERNAME] = prefs.getString(KEY_USERNAME, null) ?: ""
        // Convert long to string for consistency in return type
        user[KEY_USER_ID] = prefs.getLong(KEY_USER_ID, 0L).toString()
        return user
    }

    /**
     * Clear session details
     */
    fun logoutUser() {
        editor.clear()
        editor.commit()
    }
}
