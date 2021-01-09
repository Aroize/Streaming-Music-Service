package ru.ilia.streamer

import android.app.Application
import ru.ilia.streamer.core.PreferenceManager
import ru.ilia.streamer.core.StreamingManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        PreferenceManager.init(this)
        StreamingManager.init(this)
    }
}