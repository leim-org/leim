package cn.lemwood.leim.data.websocket

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import okio.ByteString
import cn.lemwood.leim.utils.Constants
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WebSocket管理器
 * 负责WebSocket连接的建立、维护和消息处理
 */
@Singleton
class WebSocketManager @Inject constructor() {
    
    private val TAG = "WebSocketManager"
    private val gson = Gson()
    
    private var webSocket: WebSocket? = null
    private var okHttpClient: OkHttpClient? = null
    private var reconnectJob: Job? = null
    private var heartbeatJob: Job? = null
    
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    
    private val _messageFlow = MutableStateFlow<WebSocketMessage?>(null)
    val messageFlow: StateFlow<WebSocketMessage?> = _messageFlow
    
    private var reconnectAttempts = 0
    private var serverUrl = Constants.DEFAULT_WEBSOCKET_URL
    private var authToken: String? = null
    
    enum class ConnectionState {
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        RECONNECTING,
        ERROR
    }
    
    /**
     * 连接WebSocket
     */
    fun connect(url: String, token: String? = null) {
        serverUrl = url
        authToken = token
        
        if (_connectionState.value == ConnectionState.CONNECTED || 
            _connectionState.value == ConnectionState.CONNECTING) {
            Log.d(TAG, "Already connected or connecting")
            return
        }
        
        _connectionState.value = ConnectionState.CONNECTING
        
        try {
            okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            
            val request = Request.Builder()
                .url(serverUrl)
                .apply {
                    authToken?.let { addHeader("Authorization", "Bearer $it") }
                }
                .build()
            
            webSocket = okHttpClient?.newWebSocket(request, webSocketListener)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect WebSocket", e)
            _connectionState.value = ConnectionState.ERROR
            scheduleReconnect()
        }
    }
    
    /**
     * 断开WebSocket连接
     */
    fun disconnect() {
        Log.d(TAG, "Disconnecting WebSocket")
        
        reconnectJob?.cancel()
        heartbeatJob?.cancel()
        
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
        
        okHttpClient?.dispatcher?.executorService?.shutdown()
        okHttpClient = null
        
        _connectionState.value = ConnectionState.DISCONNECTED
        reconnectAttempts = 0
    }
    
    /**
     * 发送消息
     */
    fun sendMessage(message: WebSocketMessage): Boolean {
        return try {
            val json = gson.toJson(message)
            webSocket?.send(json) ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message", e)
            false
        }
    }
    
    /**
     * 发送文本消息
     */
    fun sendTextMessage(text: String): Boolean {
        return webSocket?.send(text) ?: false
    }
    
    /**
     * WebSocket监听器
     */
    private val webSocketListener = object : WebSocketListener() {
        
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "WebSocket connected")
            _connectionState.value = ConnectionState.CONNECTED
            reconnectAttempts = 0
            
            // 发送认证消息
            authToken?.let { token ->
                val authMessage = WebSocketMessage(
                    type = Constants.WS_MESSAGE_TYPE_AUTH,
                    data = mapOf("token" to token)
                )
                sendMessage(authMessage)
            }
            
            // 开始心跳
            startHeartbeat()
        }
        
        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "Received message: $text")
            
            try {
                val message = gson.fromJson(text, WebSocketMessage::class.java)
                _messageFlow.value = message
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse message", e)
            }
        }
        
        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(TAG, "Received bytes: ${bytes.hex()}")
        }
        
        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closing: $code $reason")
            _connectionState.value = ConnectionState.DISCONNECTED
        }
        
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closed: $code $reason")
            _connectionState.value = ConnectionState.DISCONNECTED
            heartbeatJob?.cancel()
            
            // 如果不是主动断开，尝试重连
            if (code != 1000) {
                scheduleReconnect()
            }
        }
        
        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket failure", t)
            _connectionState.value = ConnectionState.ERROR
            heartbeatJob?.cancel()
            scheduleReconnect()
        }
    }
    
    /**
     * 开始心跳
     */
    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive && _connectionState.value == ConnectionState.CONNECTED) {
                try {
                    val heartbeatMessage = WebSocketMessage(
                        type = Constants.WS_MESSAGE_TYPE_HEARTBEAT,
                        data = mapOf("timestamp" to System.currentTimeMillis())
                    )
                    sendMessage(heartbeatMessage)
                    delay(30000) // 30秒心跳间隔
                } catch (e: Exception) {
                    Log.e(TAG, "Heartbeat failed", e)
                    break
                }
            }
        }
    }
    
    /**
     * 安排重连
     */
    private fun scheduleReconnect() {
        if (reconnectAttempts >= Constants.WEBSOCKET_MAX_RECONNECT_ATTEMPTS) {
            Log.w(TAG, "Max reconnect attempts reached")
            _connectionState.value = ConnectionState.ERROR
            return
        }
        
        reconnectJob?.cancel()
        reconnectJob = CoroutineScope(Dispatchers.IO).launch {
            delay(Constants.WEBSOCKET_RECONNECT_INTERVAL)
            
            if (isActive) {
                Log.d(TAG, "Attempting to reconnect (${reconnectAttempts + 1}/${Constants.WEBSOCKET_MAX_RECONNECT_ATTEMPTS})")
                _connectionState.value = ConnectionState.RECONNECTING
                reconnectAttempts++
                connect(serverUrl, authToken)
            }
        }
    }
    
    /**
     * 检查连接状态
     */
    fun isConnected(): Boolean {
        return _connectionState.value == ConnectionState.CONNECTED
    }
    
    /**
     * 重置重连计数
     */
    fun resetReconnectAttempts() {
        reconnectAttempts = 0
    }
}