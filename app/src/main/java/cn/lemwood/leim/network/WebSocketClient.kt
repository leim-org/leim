package cn.lemwood.leim.network

import android.util.Log
import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.atomic.AtomicBoolean

class WebSocketClient(
    private val userId: String,
    private val onMessageReceived: (String) -> Unit,
    private val onConnectionStatusChanged: (Boolean) -> Unit
) {
    
    companion object {
        private const val TAG = "WebSocketClient"
        private const val SERVER_URL = "ws://your-server-url:port/ws" // TODO: 替换为实际服务器地址
    }
    
    private var webSocket: WebSocketClient? = null
    private val isConnected = AtomicBoolean(false)
    private val gson = Gson()
    
    fun connect() {
        try {
            val uri = URI("$SERVER_URL?userId=$userId")
            webSocket = object : WebSocketClient(uri) {
                override fun onOpen(handshake: ServerHandshake?) {
                    Log.d(TAG, "WebSocket connected")
                    isConnected.set(true)
                    onConnectionStatusChanged(true)
                    
                    // 发送认证消息
                    sendAuthMessage()
                }
                
                override fun onMessage(message: String?) {
                    Log.d(TAG, "Received message: $message")
                    message?.let { onMessageReceived(it) }
                }
                
                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    Log.d(TAG, "WebSocket closed: $code - $reason")
                    isConnected.set(false)
                    onConnectionStatusChanged(false)
                }
                
                override fun onError(ex: Exception?) {
                    Log.e(TAG, "WebSocket error", ex)
                    isConnected.set(false)
                    onConnectionStatusChanged(false)
                }
            }
            
            webSocket?.connect()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect WebSocket", e)
            onConnectionStatusChanged(false)
        }
    }
    
    fun disconnect() {
        webSocket?.close()
        isConnected.set(false)
    }
    
    fun sendMessage(message: String) {
        if (isConnected.get()) {
            webSocket?.send(message)
        } else {
            Log.w(TAG, "WebSocket not connected, cannot send message")
        }
    }
    
    fun sendMessage(messageData: Any) {
        val json = gson.toJson(messageData)
        sendMessage(json)
    }
    
    fun isConnected(): Boolean = isConnected.get()
    
    private fun sendAuthMessage() {
        val authMessage = mapOf(
            "type" to "auth",
            "userId" to userId,
            "timestamp" to System.currentTimeMillis()
        )
        sendMessage(authMessage)
    }
}