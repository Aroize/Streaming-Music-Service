package ru.ilia.streamer.data

import android.content.Intent

data class StreamingBundle(
        val intent: Intent,
        val dimension: Int
)