package ru.ilia.streamer.data

import android.content.Context

data class StreamingServiceInfo(
    val clientName: String,
    val serviceId: String
) {
    constructor(
        clientName: String,
        context: Context
    ): this(clientName, context.applicationContext.packageName)
}