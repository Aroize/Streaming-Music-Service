package ru.ilia.streamer.data

import android.media.AudioFormat
import java.lang.IllegalArgumentException

object AudioSettings {
    const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
    const val SAMPLE_RATE = 44100
    const val CHANNEL_IN_MASK = AudioFormat.CHANNEL_IN_STEREO

    fun bytesCountPerFrame(): Int {
        return when(ENCODING) {
            AudioFormat.ENCODING_PCM_8BIT -> 1
            AudioFormat.ENCODING_PCM_16BIT -> 2
            else -> throw IllegalArgumentException()
        }
    }
}