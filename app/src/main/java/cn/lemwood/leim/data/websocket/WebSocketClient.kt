package cn.lemwood.leim.data.websocket

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

/**
 * WebSocket 客户端
 */
class LeimWebSocketClient(
    serverUri: URI,
    private val listener: WebSocketListener
) : WebSocketClient(serverUri) {
    
    companion object {
        private const val TAG = "LeimWebSocketClient"
    }
    
    interface WebSocketListener {
        fun onConnected()
        fun onDisconnected()
        fun onMessageReceived(message: String)
        fun onError(error: String)
    }
    
    override fun onOpen(handshake: ServerHandshake?) {
        Log.d(TAG, "WebSocket 连接已建立")
        listener.onConnected()
    }
    
    override fun onMessage(message: String?) {
        Log.d(TAG, "收到消息: $message")
        message?.let { listener.onMessageReceived(it) }
    }
    
    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d(TAG, "WebSocket 连接已关闭: $reason")
        listener.onDisconnected()
    }
    
    override fun onError(ex: Exception?) {
        Log.e(TAG, "WebSocket 错误", ex)
        listener.onError(ex?.message ?: "未知错误")
    }
    
    /**
     * 发送文本消息
     */
    fun sendTextMessage(conversationId: String, content: String) {
        val messageJson = JSONObject().apply {
            put("type", "text_message")
            put("conversationId", conversationId)
            put("content", content)
            put("timestamp", System.currentTimeMillis())
        }
        send(messageJson.toString())
    }
    
    /**
     * 发送心跳包
     */
    fun sendHeartbeat() {
        val heartbeatJson = JSONObject().apply {
            put("type", "heartbeat")
            put("timestamp", System.currentTimeMillis())
        }
        send(heartbeatJson.toString())
    }
    
    /**
     * 发送用户状态更新
     */
    fun sendStatusUpdate(status: String) {
        val statusJson = JSONObject().apply {
            put("type", "status_update")
            put("status", status)
            put("timestamp", System.currentTimeMillis())
        }
        send(statusJson.toString())
    }
}