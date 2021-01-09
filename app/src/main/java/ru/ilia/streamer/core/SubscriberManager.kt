package ru.ilia.streamer.core

import android.util.Log
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload

class SubscriberManager {
    private val subscribers = HashSet<String>()

    fun addSubscriber(id: String) {
        subscribers.add(id)
    }

    fun removeSubscriber(id: String) {
        subscribers.remove(id)
    }

    fun sendPayloadToAllSubscribers(connectionsClient: ConnectionsClient, payload: Payload) {
        Log.d("SubscriberManager", "On new Data")
        subscribers.forEach {  client ->
            connectionsClient.sendPayload(client, payload)
        }
    }

    fun clearSubscribers() {
        subscribers.clear()
    }
}