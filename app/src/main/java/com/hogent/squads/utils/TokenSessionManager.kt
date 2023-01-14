package com.hogent.squads.utils

import android.content.Context
import android.content.SharedPreferences
import com.hogent.squads.R
import com.hogent.squads.utils.ContextProvider.Companion.applicationContext

/**
 * Session manager to save and fetch data from SharedPreferences
 */
class TokenSessionManager {
    private var prefs: SharedPreferences =
        applicationContext.getSharedPreferences(applicationContext.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }
    fun saveUserId(id: String) {
        val editor = prefs.edit()
        editor.putString(USER_ID, id)
        editor.apply()
    }

    fun deleteAuthToken() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.apply()
    }
    fun deleteAuthId() {
        val editor = prefs.edit()
        editor.remove(USER_ID)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
    fun fetchUserId(): String? {
        return prefs.getString(USER_ID, null)
    }
}
