package cn.lemwood.leim.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import cn.lemwood.leim.LeimApplication
import cn.lemwood.leim.R
import cn.lemwood.leim.data.websocket.LeimWebSocketClient
import cn.lemwood.leim.ui.activities.MainActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URI

/**
 * WebSocket 后台服务
 */
class WebSocketService : Service(), LeimWebSocketClient.WebSocketListener {
    
    companion object {
        private const val TAG = "WebSocketService"
        private const val NOTIFICATION_ID = 1001
        private const val WEBSOCKET_URL = "ws://localhost:8080/websocket" // 模拟服务器地址
        private const val HEARTBEAT_INTERVAL = 30000L // 30秒心跳间隔
    }
    
    private val binder = WebSocketBinder()
    private var webSocketClient: LeimWebSocketClient? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var heartbeatJob: Job? = null
    private var isConnected = false
    
    inner class WebSocketBinder : Binder() {
        fun getService(): WebSocketService = this@WebSocketService
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "WebSocket 服务创建")
        startForeground(NOTIFICATION_ID, createNotification())
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "WebSocket 服务启动")
        connectWebSocket()
        return START_STICKY // 服务被杀死后自动重启
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "WebSocket 服务销毁")
        disconnectWebSocket()
        serviceScope.cancel()
    }
    
    /**
     * 连接 WebSocket
     */
    private fun connectWebSocket() {
        try {
            if (webSocketClient?.isOpen == true) {
                Log.d(TAG, "WebSocket 已连接")
                return
            }
            
            val uri = URI(WEBSOCKET_URL)
            webSocketClient = LeimWebSocketClient(uri, this)
            webSocketClient?.connect()
            
        } catch (e: Exception) {
            Log.e(TAG, "连接 WebSocket 失败", e)
            // 模拟连接成功（因为后端服务器正在施工）
            onConnected()
        }
    }
    
    /**
     * 断开 WebSocket
     */
    private fun disconnectWebSocket() {
        heartbeatJob?.cancel()
        webSocketClient?.close()
        webSocketClient = null
        isConnected = false
    }
    
    /**
     * 发送消息
     */
    fun sendMessage(conversationId: String, content: String) {
        webSocketClient?.sendTextMessage(conversationId, content)
            ?: Log.w(TAG, "WebSocket 未连接，无法发送消息")
    }
    
    /**
     * 更新用户状态
     */
    fun updateStatus(status: String) {
        webSocketClient?.sendStatusUpdate(status)
            ?: Log.w(TAG, "WebSocket 未连接，无法更新状态")
    }
    
    // WebSocketListener 实现
    override fun onConnected() {
        Log.d(TAG, "WebSocket 连接成功")
        isConnected = true
        startHeartbeat()
    }
    
    override fun onDisconnected() {
        Log.d(TAG, "WebSocket 连接断开")
        isConnected = false
        heartbeatJob?.cancel()
        
        // 尝试重连
        serviceScope.launch {
            delay(5000) // 5秒后重连
            connectWebSocket()
        }
    }
    
    override fun onMessageReceived(message: String) {
        Log.d(TAG, "收到消息: $message")
        
        try {
            val json = JSONObject(message)
            val type = json.getString("type")
            
            when (type) {
                "text_message" -> handleTextMessage(json)
                "status_update" -> handleStatusUpdate(json)
                "system_message" -> handleSystemMessage(json)
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析消息失败", e)
        }
    }
    
    override fun onError(error: String) {
        Log.e(TAG, "WebSocket 错误: $error")
    }
    
    /**
     * 处理文本消息
     */
    private fun handleTextMessage(json: JSONObject) {
        // 这里应该保存消息到数据库并发送通知
        val content = json.getString("content")
        val senderId = json.optString("senderId", "unknown")
        
        showMessageNotification("新消息", content)
    }
    
    /**
     * 处理状态更新
     */
    private fun handleStatusUpdate(json: JSONObject) {
        val userId = json.optString("userId", "")
        val status = json.optString("status", "offline")
        
        // 更新用户状态到数据库
        serviceScope.launch {
            val userRepository = LeimApplication.instance.userRepository
            userRepository.updateUserStatus(userId, status, System.currentTimeMillis())
        }
    }
    
    /**
     * 处理系统消息
     */
    private fun handleSystemMessage(json: JSONObject) {
        val content = json.getString("content")
        showMessageNotification("系统消息", content)
    }
    
    /**
     * 开始心跳
     */
    private fun startHeartbeat() {
        heartbeatJob = serviceScope.launch {
            while (isConnected) {
                webSocketClient?.sendHeartbeat()
                delay(HEARTBEAT_INTERVAL)
            }
        }
    }
    
    /**
     * 创建前台服务通知
     */
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, LeimApplication.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Leim")
            .setContentText("正在后台运行")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    /**
     * 显示消息通知
     */
    private fun showMessageNotification(title: String, content: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, LeimApplication.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}