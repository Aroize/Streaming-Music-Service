package ru.ilia.streamer.core

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {

    private const val name = "__main_preference_file__"

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun getBoolean(key: String) = preferences.getBoolean(key, false)

    fun setBoolean(key: String, value: Boolean) = preferences.edit().putBoolean(key, value).apply()
}