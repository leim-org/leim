package cn.lemwood.leim.data.websocket

import com.google.gson.annotations.SerializedName

/**
 * WebSocket消息数据类
 * 定义WebSocket通信的消息格式
 */
data class WebSocketMessage(
    @SerializedName("type")
    val type: String,
    
    @SerializedName("data")
    val data: Map<String, Any>? = null,
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("from")
    val from: String? = null,
    
    @SerializedName("to")
    val to: String? = null
) {
    
    companion object {
        /**
         * 创建认证消息
         */
        fun createAuthMessage(token: String): WebSocketMessage {
            return WebSocketMessage(
                type = "auth",
                data = mapOf("token" to token)
            )
        }
        
        /**
         * 创建聊天消息
         */
        fun createChatMessage(
            content: String,
            to: String,
            messageType: String = "text",
            conversationId: String? = null
        ): WebSocketMessage {
            val data = mutableMapOf<String, Any>(
                "content" to content,
                "messageType" to messageType
            )
            
            conversationId?.let { data["conversationId"] = it }
            
            return WebSocketMessage(
                type = "chat",
                data = data,
                to = to
            )
        }
        
        /**
         * 创建心跳消息
         */
        fun createHeartbeatMessage(): WebSocketMessage {
            return WebSocketMessage(
                type = "heartbeat",
                data = mapOf("timestamp" to System.currentTimeMillis())
            )
        }
        
        /**
         * 创建状态消息
         */
        fun createStatusMessage(status: String): WebSocketMessage {
            return WebSocketMessage(
                type = "status",
                data = mapOf("status" to status)
            )
        }
        
        /**
         * 创建用户上线消息
         */
        fun createUserOnlineMessage(): WebSocketMessage {
            return WebSocketMessage(
                type = "user_online",
                data = mapOf("timestamp" to System.currentTimeMillis())
            )
        }
        
        /**
         * 创建用户下线消息
         */
        fun createUserOfflineMessage(): WebSocketMessage {
            return WebSocketMessage(
                type = "user_offline",
                data = mapOf("timestamp" to System.currentTimeMillis())
            )
        }
        
        /**
         * 创建消息已读回执
         */
        fun createMessageReadReceipt(messageId: String, conversationId: String): WebSocketMessage {
            return WebSocketMessage(
                type = "message_read",
                data = mapOf(
                    "messageId" to messageId,
                    "conversationId" to conversationId
                )
            )
        }
        
        /**
         * 创建消息送达回执
         */
        fun createMessageDeliveredReceipt(messageId: String): WebSocketMessage {
            return WebSocketMessage(
                type = "message_delivered",
                data = mapOf("messageId" to messageId)
            )
        }
        
        /**
         * 创建正在输入消息
         */
        fun createTypingMessage(conversationId: String, isTyping: Boolean): WebSocketMessage {
            return WebSocketMessage(
                type = "typing",
                data = mapOf(
                    "conversationId" to conversationId,
                    "isTyping" to isTyping
                )
            )
        }
    }
    
    /**
     * 获取数据字段的值
     */
    fun getDataValue(key: String): Any? {
        return data?.get(key)
    }
    
    /**
     * 获取字符串类型的数据值
     */
    fun getDataString(key: String): String? {
        return data?.get(key) as? String
    }
    
    /**
     * 获取整数类型的数据值
     */
    fun getDataInt(key: String): Int? {
        return when (val value = data?.get(key)) {
            is Int -> value
            is Double -> value.toInt()
            is String -> value.toIntOrNull()
            else -> null
        }
    }
    
    /**
     * 获取长整数类型的数据值
     */
    fun getDataLong(key: String): Long? {
        return when (val value = data?.get(key)) {
            is Long -> value
            is Double -> value.toLong()
            is String -> value.toLongOrNull()
            else -> null
        }
    }
    
    /**
     * 获取布尔类型的数据值
     */
    fun getDataBoolean(key: String): Boolean? {
        return when (val value = data?.get(key)) {
            is Boolean -> value
            is String -> value.toBooleanStrictOrNull()
            else -> null
        }
    }
    
    /**
     * 检查是否为特定类型的消息
     */
    fun isType(messageType: String): Boolean {
        return type == messageType
    }
    
    /**
     * 检查是否为聊天消息
     */
    fun isChatMessage(): Boolean = isType("chat")
    
    /**
     * 检查是否为认证消息
     */
    fun isAuthMessage(): Boolean = isType("auth")
    
    /**
     * 检查是否为心跳消息
     */
    fun isHeartbeatMessage(): Boolean = isType("heartbeat")
    
    /**
     * 检查是否为状态消息
     */
    fun isStatusMessage(): Boolean = isType("status")
}