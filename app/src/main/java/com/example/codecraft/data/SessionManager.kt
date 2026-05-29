package com.example.codecraft.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Хранит ID вошедшего пользователя между сессиями.
 * При выходе — очищает.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("codecraft_session", Context.MODE_PRIVATE)

    var userId: Long?
        get() {
            val id = prefs.getLong(KEY_USER_ID, -1L)
            return if (id == -1L) null else id
        }
        set(value) {
            if (value == null) prefs.edit().remove(KEY_USER_ID).apply()
            else prefs.edit().putLong(KEY_USER_ID, value).apply()
        }

    val isLoggedIn: Boolean get() = userId != null

    fun logout() {
        userId = null
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
    }
}
