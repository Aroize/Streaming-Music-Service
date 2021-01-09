package ru.ilia.streamer.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import ru.ilia.streamer.data.AudioSettings
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread

class RecordingManager(
        private val context: Context,
        private val listener: OnNewRecordingBatchListener
) {
    interface OnNewRecordingBatchListener {
        fun onNewRecordingBatch(byteArray: ByteArray)
    }

    private var audioCaptureThread: Thread? = null

    fun startRecording(projectionIntent: Intent) {
        val mediaProjection = getMediaProjection(Activity.RESULT_OK, projectionIntent)
        val config = createAudioPlaybackCaptureConfig(mediaProjection)
        val audioFormat = createAudioFormat()
        val audioRecord = createAudioRecord(audioFormat, config)
        startListeningToRecord(audioRecord)
    }

    private fun startListeningToRecord(audioRecord: AudioRecord) {
        audioCaptureThread = thread(start = true) {
            while (audioCaptureThread?.isInterrupted != true) {
                val byteBuffer = ByteArray(audioRecord.bufferSizeInFrames * AudioSettings.bytesCountPerFrame())
                audioRecord.read(byteBuffer, 0, byteBuffer.size)
                listener.onNewRecordingBatch(byteBuffer)
            }
        }
    }

    private fun getMediaProjection(resultCode: Int, projectionIntent: Intent): MediaProjection {
        val mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        return mediaProjectionManager.getMediaProjection(resultCode, projectionIntent)
    }

    @SuppressLint("NewApi")
    private fun createAudioPlaybackCaptureConfig(
            mediaProjection: MediaProjection
    ): AudioPlaybackCaptureConfiguration {
        return AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .build()
    }

    private fun createAudioFormat(): AudioFormat {
        return AudioFormat.Builder()
                .setEncoding(AudioSettings.ENCODING)
                .setSampleRate(AudioSettings.SAMPLE_RATE)
                .setChannelMask(AudioSettings.CHANNEL_IN_MASK)
                .build()
    }

    @SuppressLint("NewApi")
    private fun createAudioRecord(audioFormat: AudioFormat, config: AudioPlaybackCaptureConfiguration): AudioRecord {
        val minBufferSizeInBytes = AudioRecord.getMinBufferSize(
                AudioSettings.SAMPLE_RATE, AudioSettings.CHANNEL_IN_MASK, AudioSettings.ENCODING
        )
        return AudioRecord.Builder()
                .setAudioFormat(audioFormat)
                .setAudioPlaybackCaptureConfig(config)
                .setBufferSizeInBytes(2 * minBufferSizeInBytes)
                .build()

    }

    fun stopRecording() {
        audioCaptureThread?.interrupt()
        audioCaptureThread?.join()
        audioCaptureThread = null
    }
}