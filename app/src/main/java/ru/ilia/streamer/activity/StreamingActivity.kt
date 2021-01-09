package ru.ilia.streamer.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ru.ilia.streamer.R
import ru.ilia.streamer.core.StreamingManager
import ru.ilia.streamer.data.StreamingBundle
import ru.ilia.streamer.data.StreamingServiceInfo

class StreamingActivity : AppCompatActivity() {

    private var isStreaming = false
    private var isPermissionGranted = false

    private lateinit var startStreamingBtn: Button
    private lateinit var stopStreamingBtn: Button
    private lateinit var qrView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.streaming_activity)
        startStreamingBtn = findViewById(R.id.start_streaming)
        startStreamingBtn.setOnClickListener {
            startStreaming()
        }
        stopStreamingBtn = findViewById(R.id.stop_streaming)
        stopStreamingBtn.setOnClickListener {
            stopStreaming()
        }
        qrView = findViewById(R.id.qr_view)
        if (isRecordAudioGranted().not()) {
            requestAudioPermission()
        } else {
            isPermissionGranted = true
            isStreaming = StreamingManager.qrCode != null
            resetUi()
        }
    }

    private fun startStreaming() {
        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), MEDIA_PROJECTION_REQUEST_CODE)
    }

    private fun startStreaming(projectionIntent: Intent) {
        StreamingManager.startStreaming(StreamingBundle(projectionIntent, getDimension()))
        isStreaming = true
        resetUi()
    }

    private fun stopStreaming() {
        StreamingManager.stopStreaming()
        isStreaming = false
        resetUi()
    }

    private fun getDimension(): Int {
        val point = Point()
        display!!.getRealSize(point)
        return if (point.x < point.y) point.x else point.y
    }

    private fun isRecordAudioGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                permissions.forEachIndexed { index, _ ->
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                }
                isPermissionGranted = true
                resetUi()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            MEDIA_PROJECTION_REQUEST_CODE -> {
                if (resultCode == RESULT_OK && data != null) {
                    startStreaming(data)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun resetUi() {
        startStreamingBtn.isEnabled = isPermissionGranted && !isStreaming
        stopStreamingBtn.isEnabled = isPermissionGranted && isStreaming
        if (isStreaming) {
            qrView.setImageBitmap(StreamingManager.qrCode)
        } else {
            qrView.setImageBitmap(null)
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
        private const val MEDIA_PROJECTION_REQUEST_CODE = 321

        private const val tag = "StreamingActivity"

        fun launch(context: Context) {
            val intent = Intent(context, StreamingActivity::class.java)
            context.startActivity(intent)
        }
    }
}