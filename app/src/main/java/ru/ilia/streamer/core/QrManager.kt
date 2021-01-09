package ru.ilia.streamer.core

import android.graphics.Bitmap
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import ru.ilia.streamer.data.StreamingServiceInfo

class QrManager {

    var bitmap: Bitmap? = null
    private set

    fun generateQrCode(serviceInfo: StreamingServiceInfo, dimension: Int) {
        val qrGeneratorEncoder = QRGEncoder(serviceInfo.clientName, null, QRGContents.Type.TEXT, dimension)
        bitmap = qrGeneratorEncoder.encodeAsBitmap()
    }

    fun clearQr() {
        bitmap = null
    }
}