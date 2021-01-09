package ru.ilia.streamer.core

import android.os.Build

object OsUtils {
    fun isStreamingAvailable() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun isNotificationChannelNeeded() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}