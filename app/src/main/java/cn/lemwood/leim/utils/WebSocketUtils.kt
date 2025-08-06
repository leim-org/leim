package cn.lemwood.leim.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.net.URI
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * WebSocket 工具类
 * 提供 WebSocket 连接管理和消息处理的辅助功能
 */
object WebSocketUtils {
    
    private val gson = Gson()
    
    /**
     * WebSocket 连接状态
     */
    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        RECONNECTING,
        ERROR
    }
    
    /**
     * 消息类型
     */
    enum class MessageType {
        TEXT,
        IMAGE,
        AUDIO,
        VIDEO,
        FILE,
        SYSTEM,
        HEARTBEAT,
        ACK
    }
    
    /**
     * WebSocket 消息数据类
     */
    data class WebSocketMessage(
        val id: String,
        val type: MessageType,
        val from: String,
        val to: String,
        val content: String,
        val timestamp: Long,
        val extra: Map<String, Any>? = null
    )
    
    /**
     * 心跳消息数据类
     */
    data class HeartbeatMessage(
        val type: String = "heartbeat",
        val timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * 确认消息数据类
     */
    data class AckMessage(
        val type: String = "ack",
        val messageId: String,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * 检查网络连接状态
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected == true
        }
    }
    
    /**
     * 验证 WebSocket URL 格式
     */
    fun isValidWebSocketUrl(url: String): Boolean {
        return try {
            val uri = URI.create(url)
            uri.scheme in listOf("ws", "wss") && uri.host != null
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 构建 WebSocket URL
     */
    fun buildWebSocketUrl(host: String, port: Int, path: String = "", useSSL: Boolean = false): String {
        val scheme = if (useSSL) "wss" else "ws"
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        return "$scheme://$host:$port$normalizedPath"
    }
    
    /**
     * 生成消息 ID
     */
    fun generateMessageId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (Math.random() * 10000).toInt()
        return "${timestamp}_$random"
    }
    
    /**
     * 创建文本消息
     */
    fun createTextMessage(from: String, to: String, content: String): WebSocketMessage {
        return WebSocketMessage(
            id = generateMessageId(),
            type = MessageType.TEXT,
            from = from,
            to = to,
            content = content,
            timestamp = System.currentTimeMillis()
        )
    }
    
    /**
     * 创建系统消息
     */
    fun createSystemMessage(content: String): WebSocketMessage {
        return WebSocketMessage(
            id = generateMessageId(),
            type = MessageType.SYSTEM,
            from = "system",
            to = "all",
            content = content,
            timestamp = System.currentTimeMillis()
        )
    }
    
    /**
     * 创建心跳消息
     */
    fun createHeartbeatMessage(): String {
        return gson.toJson(HeartbeatMessage())
    }
    
    /**
     * 创建确认消息
     */
    fun createAckMessage(messageId: String): String {
        return gson.toJson(AckMessage(messageId = messageId))
    }
    
    /**
     * 将消息对象转换为 JSON 字符串
     */
    fun messageToJson(message: WebSocketMessage): String {
        return gson.toJson(message)
    }
    
    /**
     * 将 JSON 字符串转换为消息对象
     */
    fun jsonToMessage(json: String): WebSocketMessage? {
        return try {
            gson.fromJson(json, WebSocketMessage::class.java)
        } catch (e: JsonSyntaxException) {
            LogUtils.error("WebSocket", "Failed to parse message JSON: $json", e)
            null
        }
    }
    
    /**
     * 检查是否为心跳消息
     */
    fun isHeartbeatMessage(json: String): Boolean {
        return try {
            val message = gson.fromJson(json, Map::class.java)
            message["type"] == "heartbeat"
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 检查是否为确认消息
     */
    fun isAckMessage(json: String): Boolean {
        return try {
            val message = gson.fromJson(json, Map::class.java)
            message["type"] == "ack"
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 计算重连延迟时间（指数退避算法）
     */
    fun calculateReconnectDelay(attemptCount: Int, baseDelay: Long = 1000): Long {
        val maxDelay = TimeUnit.MINUTES.toMillis(5) // 最大延迟 5 分钟
        val delay = baseDelay * Math.pow(2.0, attemptCount.toDouble()).toLong()
        return minOf(delay, maxDelay)
    }
    
    /**
     * 验证消息格式
     */
    fun isValidMessage(message: WebSocketMessage): Boolean {
        return message.id.isNotEmpty() &&
                message.from.isNotEmpty() &&
                message.to.isNotEmpty() &&
                message.timestamp > 0
    }
    
    /**
     * 压缩消息内容
     */
    fun compressMessage(content: String): String {
        // 简单的压缩策略：移除多余的空白字符
        return content.trim().replace(Regex("\\s+"), " ")
    }
    
    /**
     * 计算消息哈希值
     */
    fun calculateMessageHash(message: WebSocketMessage): String {
        val content = "${message.id}${message.type}${message.from}${message.to}${message.content}${message.timestamp}"
        return calculateMD5(content)
    }
    
    /**
     * 计算字符串的 MD5 哈希值
     */
    private fun calculateMD5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * 格式化连接状态为可读字符串
     */
    fun formatConnectionState(state: ConnectionState): String {
        return when (state) {
            ConnectionState.DISCONNECTED -> "已断开"
            ConnectionState.CONNECTING -> "连接中"
            ConnectionState.CONNECTED -> "已连接"
            ConnectionState.RECONNECTING -> "重连中"
            ConnectionState.ERROR -> "连接错误"
        }
    }
    
    /**
     * 检查消息是否过期
     */
    fun isMessageExpired(timestamp: Long, expirationTime: Long = TimeUnit.HOURS.toMillis(24)): Boolean {
        return System.currentTimeMillis() - timestamp > expirationTime
    }
    
    /**
     * 创建连接参数
     */
    fun createConnectionParams(userId: String, token: String): Map<String, String> {
        return mapOf(
            "userId" to userId,
            "token" to token,
            "timestamp" to System.currentTimeMillis().toString(),
            "version" to "1.0"
        )
    }
    
    /**
     * 构建带参数的 WebSocket URL
     */
    fun buildUrlWithParams(baseUrl: String, params: Map<String, String>): String {
        if (params.isEmpty()) return baseUrl
        
        val queryString = params.entries.joinToString("&") { "${it.key}=${it.value}" }
        return "$baseUrl?$queryString"
    }
    
    /**
     * 解析错误代码
     */
    fun parseErrorCode(errorMessage: String): Int {
        return try {
            val regex = Regex("code:(\\d+)")
            val matchResult = regex.find(errorMessage)
            matchResult?.groupValues?.get(1)?.toInt() ?: -1
        } catch (e: Exception) {
            -1
        }
    }
    
    /**
     * 格式化错误消息
     */
    fun formatErrorMessage(code: Int, message: String): String {
        return when (code) {
            1000 -> "正常关闭"
            1001 -> "端点离开"
            1002 -> "协议错误"
            1003 -> "不支持的数据类型"
            1006 -> "连接异常关闭"
            1007 -> "数据格式错误"
            1008 -> "策略违规"
            1009 -> "消息过大"
            1010 -> "扩展协商失败"
            1011 -> "服务器错误"
            else -> "未知错误: $message"
        }
    }
    
    /**
     * 创建文件消息元数据
     */
    fun createFileMessageExtra(fileName: String, fileSize: Long, mimeType: String): Map<String, Any> {
        return mapOf(
            "fileName" to fileName,
            "fileSize" to fileSize,
            "mimeType" to mimeType,
            "uploadTime" to System.currentTimeMillis()
        )
    }
    
    /**
     * 检查是否需要发送心跳
     */
    fun shouldSendHeartbeat(lastHeartbeatTime: Long, interval: Long = TimeUnit.SECONDS.toMillis(30)): Boolean {
        return System.currentTimeMillis() - lastHeartbeatTime >= interval
    }
    
    /**
     * 获取连接超时时间
     */
    fun getConnectionTimeout(): Long = TimeUnit.SECONDS.toMillis(10)
    
    /**
     * 获取读取超时时间
     */
    fun getReadTimeout(): Long = TimeUnit.SECONDS.toMillis(30)
    
    /**
     * 获取写入超时时间
     */
    fun getWriteTimeout(): Long = TimeUnit.SECONDS.toMillis(10)
}