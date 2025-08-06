package cn.lemwood.leim.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import cn.lemwood.leim.LeimApplication
import cn.lemwood.leim.R
import cn.lemwood.leim.network.WebSocketClient
import cn.lemwood.leim.ui.MainActivity
import cn.lemwood.leim.utils.PreferenceManager
import kotlinx.coroutines.*

class WebSocketService : Service() {
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val RECONNECT_DELAY = 5000L // 5秒重连间隔
    }
    
    private var webSocketClient: WebSocketClient? = null
    private lateinit var preferenceManager: PreferenceManager
    private var reconnectJob: Job? = null
    
    override fun onCreate() {
        super.onCreate()
        preferenceManager = PreferenceManager(this)
        createForegroundNotification()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (preferenceManager.isLoggedIn()) {
            connectWebSocket()
        }
        return START_STICKY // 服务被杀死后自动重启
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createForegroundNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, LeimApplication.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Leim")
            .setContentText("正在后台运行")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        startForeground(NOTIFICATION_ID, notification)
    }
    
    private fun connectWebSocket() {
        if (webSocketClient?.isConnected() == true) {
            return
        }
        
        val userInfo = preferenceManager.getUserInfo()
        if (userInfo != null) {
            webSocketClient = WebSocketClient(
                userId = userInfo.leimId,
                onMessageReceived = { message ->
                    // 处理接收到的消息
                    handleReceivedMessage(message)
                },
                onConnectionStatusChanged = { isConnected ->
                    handleConnectionStatusChanged(isConnected)
                }
            )
            webSocketClient?.connect()
        }
    }
    
    private fun handleReceivedMessage(message: String) {
        // TODO: 解析消息并保存到数据库
        // TODO: 显示通知
    }
    
    private fun handleConnectionStatusChanged(isConnected: Boolean) {
        if (!isConnected) {
            // 连接断开，尝试重连
            scheduleReconnect()
        } else {
            // 连接成功，取消重连任务
            reconnectJob?.cancel()
        }
    }
    
    private fun scheduleReconnect() {
        reconnectJob?.cancel()
        reconnectJob = CoroutineScope(Dispatchers.IO).launch {
            delay(RECONNECT_DELAY)
            if (isActive) {
                connectWebSocket()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        webSocketClient?.disconnect()
        reconnectJob?.cancel()
    }
}