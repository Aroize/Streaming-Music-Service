package ru.ilia.streamer.core

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {

    private object PreferenceKeys {
        const val HAD_LAUNCH = "launch_tag"
    }

    private const val name = "__main_preference_file__"

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun hasBeenLaunchedBefore(): Boolean = preferences.getBoolean(PreferenceKeys.HAD_LAUNCH, false)

    fun setHasBeenLaunchedBefore(value: Boolean = true) = preferences.edit().putBoolean(PreferenceKeys.HAD_LAUNCH, value).apply()
}