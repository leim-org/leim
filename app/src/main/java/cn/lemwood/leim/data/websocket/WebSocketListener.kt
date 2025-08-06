package cn.lemwood.leim.data.websocket

/**
 * WebSocket事件监听器接口
 * 定义WebSocket连接状态和消息处理的回调方法
 */
interface WebSocketListener {
    
    /**
     * 连接打开时调用
     */
    fun onConnected()
    
    /**
     * 连接关闭时调用
     * @param code 关闭代码
     * @param reason 关闭原因
     */
    fun onDisconnected(code: Int, reason: String)
    
    /**
     * 接收到消息时调用
     * @param message WebSocket消息
     */
    fun onMessageReceived(message: WebSocketMessage)
    
    /**
     * 发生错误时调用
     * @param error 错误信息
     */
    fun onError(error: Throwable)
    
    /**
     * 连接状态改变时调用
     * @param isConnected 是否已连接
     */
    fun onConnectionStateChanged(isConnected: Boolean)
    
    /**
     * 重连开始时调用
     * @param attempt 重连次数
     */
    fun onReconnecting(attempt: Int)
    
    /**
     * 重连成功时调用
     */
    fun onReconnected()
    
    /**
     * 重连失败时调用
     * @param maxAttempts 最大重连次数
     */
    fun onReconnectFailed(maxAttempts: Int)
    
    /**
     * 认证成功时调用
     */
    fun onAuthSuccess()
    
    /**
     * 认证失败时调用
     * @param reason 失败原因
     */
    fun onAuthFailed(reason: String)
    
    /**
     * 心跳响应时调用
     */
    fun onHeartbeatResponse()
    
    /**
     * 用户状态更新时调用
     * @param userId 用户ID
     * @param status 用户状态
     */
    fun onUserStatusUpdate(userId: String, status: String)
    
    /**
     * 消息送达回执时调用
     * @param messageId 消息ID
     */
    fun onMessageDelivered(messageId: String)
    
    /**
     * 消息已读回执时调用
     * @param messageId 消息ID
     * @param conversationId 会话ID
     */
    fun onMessageRead(messageId: String, conversationId: String)
    
    /**
     * 正在输入状态更新时调用
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param isTyping 是否正在输入
     */
    fun onTypingStatusUpdate(conversationId: String, userId: String, isTyping: Boolean)
}

/**
 * WebSocket监听器的默认实现
 * 提供空实现，子类可以选择性重写需要的方法
 */
abstract class DefaultWebSocketListener : WebSocketListener {
    
    override fun onConnected() {}
    
    override fun onDisconnected(code: Int, reason: String) {}
    
    override fun onMessageReceived(message: WebSocketMessage) {}
    
    override fun onError(error: Throwable) {}
    
    override fun onConnectionStateChanged(isConnected: Boolean) {}
    
    override fun onReconnecting(attempt: Int) {}
    
    override fun onReconnected() {}
    
    override fun onReconnectFailed(maxAttempts: Int) {}
    
    override fun onAuthSuccess() {}
    
    override fun onAuthFailed(reason: String) {}
    
    override fun onHeartbeatResponse() {}
    
    override fun onUserStatusUpdate(userId: String, status: String) {}
    
    override fun onMessageDelivered(messageId: String) {}
    
    override fun onMessageRead(messageId: String, conversationId: String) {}
    
    override fun onTypingStatusUpdate(conversationId: String, userId: String, isTyping: Boolean) {}
}