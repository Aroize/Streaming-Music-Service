package ru.ilia.streamer.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ru.ilia.streamer.core.OsUtils

class StreamingForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (OsUtils.isNotificationChannelNeeded()) {
            createNotificationChannel()
        }
        startForeground(SERVICE_ID, NotificationCompat.Builder(this, CHANNEL_ID).build())
    }

    @SuppressLint("NewApi")
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }

    companion object {
        private const val SERVICE_ID = 123321
        private const val CHANNEL_ID = "StreamingForegroundService::Channel"
        private const val CHANNEL_NAME = "StreamingForegroundService::Name"
    }
}