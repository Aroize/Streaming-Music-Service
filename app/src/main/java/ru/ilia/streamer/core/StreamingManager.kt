package ru.ilia.streamer.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.Point
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import ru.ilia.streamer.data.StreamingBundle
import ru.ilia.streamer.data.StreamingServiceInfo
import ru.ilia.streamer.service.StreamingForegroundService
import java.lang.IllegalStateException
import java.nio.charset.StandardCharsets
import kotlin.random.Random

object StreamingManager : RecordingManager.OnNewRecordingBatchListener {

    private var serviceInfo: StreamingServiceInfo? = null
    private lateinit var context: Context
    private lateinit var recordingManager: RecordingManager
    private val qrManager = QrManager()
    private val subscriberManager = SubscriberManager()
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) = Unit
        override fun onPayloadTransferUpdate(endpointId: String, p1: PayloadTransferUpdate) = Unit
    }
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {

        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            val serviceInfo = serviceInfo ?: return

            val connectionsClient = Nearby.getConnectionsClient(context)
            if (connectionInfo.endpointName == serviceInfo.clientName) {
                connectionsClient
                    .acceptConnection(endpointId, payloadCallback)
            } else {
                connectionsClient
                    .rejectConnection(endpointId)
            }
        }

        override fun onConnectionResult(endpointId: String, connectionInfo: ConnectionResolution) {
            when (connectionInfo.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    subscriberManager.addSubscriber(endpointId)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> Unit
                ConnectionsStatusCodes.STATUS_ERROR -> Unit
            }
        }

        override fun onDisconnected(endpointId: String) = subscriberManager.removeSubscriber(endpointId)
    }
    private val serviceConnection = object : ServiceConnection {

        lateinit var projectionIntent: Intent

        override fun onNullBinding(name: ComponentName?) {
            recordingManager.startRecording(projectionIntent)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            if (qrCode != null) {
                startForegroundService(projectionIntent)
            }
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) = Unit
    }

    val qrCode: Bitmap?
        get() = qrManager.bitmap

    // <===------===>

    fun init(applicationContext: Context) {
        this.context = applicationContext
        recordingManager = RecordingManager(applicationContext, this)
    }

    fun startStreaming(streamingBundle: StreamingBundle) {
        serviceInfo = startAdvertising()
        startRecording(streamingBundle.intent)
        createQrCode(streamingBundle.dimension)
    }

    fun stopStreaming() {
        stopAdvertising()
        stopRecording()
        qrManager.clearQr()
        serviceInfo = null
    }


    override fun onNewRecordingBatch(byteArray: ByteArray) {
        val bytesPayloads = Payload.fromBytes(byteArray)
        subscriberManager
                .sendPayloadToAllSubscribers(Nearby.getConnectionsClient(context), bytesPayloads)
    }

    private fun createQrCode(dimension: Int) {
        val serviceInfo = serviceInfo ?: throw IllegalStateException("Service info is null")
        qrManager.generateQrCode(serviceInfo, dimension)
    }

    private fun stopAdvertising() {
        Nearby.getConnectionsClient(context)
            .stopAdvertising()
        subscriberManager.clearSubscribers()
    }

    private fun stopRecording() {
        stopForegroundService()
        recordingManager.stopRecording()
    }

    private fun stopForegroundService() {
        context.unbindService(serviceConnection)
    }

    private fun startAdvertising(): StreamingServiceInfo {
        val options = AdvertisingOptions.Builder()
            .setStrategy(strategy)
            .build()
        val serviceInfo = createServiceInfo(context)

        Nearby.getConnectionsClient(context)
            .startAdvertising(serviceInfo.clientName, serviceInfo.serviceId, connectionLifecycleCallback, options)
            .addOnSuccessListener { Log.d(tag, "Successfully started advertising with info = $serviceInfo") }
            .addOnFailureListener { Log.d(tag, "Failed to start advertising with info = $serviceInfo") }
        return serviceInfo
    }

    private fun createServiceInfo(context: Context): StreamingServiceInfo {
        val serviceName = generateDeviceName()
        return StreamingServiceInfo(serviceName, context)
    }

    private fun generateDeviceName(): String {
        val randomBytes = Random.Default.nextBytes(16)
        return String(randomBytes, StandardCharsets.UTF_8)
    }

    private fun startRecording(projectionIntent: Intent) {
        startForegroundService(projectionIntent)
    }

    private fun startForegroundService(projectionIntent: Intent) {
        val intent = Intent(context, StreamingForegroundService::class.java)
        serviceConnection.projectionIntent = projectionIntent
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    @JvmStatic
    private val strategy = Strategy.P2P_STAR

    private const val tag = "StreamingService"
}