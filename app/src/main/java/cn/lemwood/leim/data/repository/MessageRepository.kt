package cn.lemwood.leim.data.repository

import androidx.lifecycle.LiveData
import cn.lemwood.leim.data.database.dao.MessageDao
import cn.lemwood.leim.data.database.entities.Message
import java.util.UUID

/**
 * 消息数据仓库
 */
class MessageRepository(private val messageDao: MessageDao) {
    
    fun getMessagesByConversation(conversationId: String): LiveData<List<Message>> = 
        messageDao.getMessagesByConversation(conversationId)
    
    suspend fun getMessageById(messageId: String): Message? = messageDao.getMessageById(messageId)
    
    suspend fun insertMessage(message: Message) = messageDao.insertMessage(message)
    
    suspend fun insertMessages(messages: List<Message>) = messageDao.insertMessages(messages)
    
    suspend fun updateMessage(message: Message) = messageDao.updateMessage(message)
    
    suspend fun deleteMessage(message: Message) = messageDao.deleteMessage(message)
    
    suspend fun markMessagesAsRead(conversationId: String) = messageDao.markMessagesAsRead(conversationId)
    
    suspend fun markMessageAsSent(messageId: String) = messageDao.markMessageAsSent(messageId)
    
    suspend fun markMessageAsDelivered(messageId: String) = messageDao.markMessageAsDelivered(messageId)
    
    suspend fun getUnreadCount(conversationId: String): Int = messageDao.getUnreadCount(conversationId)
    
    suspend fun getLastMessage(conversationId: String): Message? = messageDao.getLastMessage(conversationId)
    
    suspend fun deleteMessagesByConversation(conversationId: String) = 
        messageDao.deleteMessagesByConversation(conversationId)
    
    /**
     * 发送消息
     */
    suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        receiverId: String?,
        groupId: String?,
        content: String,
        messageType: String = "text"
    ): Message {
        val message = Message(
            messageId = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            groupId = groupId,
            content = content,
            messageType = messageType,
            timestamp = System.currentTimeMillis(),
            isSent = true // 模拟发送成功
        )
        insertMessage(message)
        return message
    }
    
    /**
     * 添加模拟消息数据
     */
    suspend fun addMockMessages(conversationId: String) {
        val mockMessages = listOf(
            Message(
                messageId = UUID.randomUUID().toString(),
                conversationId = conversationId,
                senderId = "leim001",
                receiverId = "current_user",
                groupId = null,
                content = "你好！欢迎使用 Leim",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = true,
                isSent = true,
                isDelivered = true
            ),
            Message(
                messageId = UUID.randomUUID().toString(),
                conversationId = conversationId,
                senderId = "current_user",
                receiverId = "leim001",
                groupId = null,
                content = "谢谢！这个应用看起来很不错",
                timestamp = System.currentTimeMillis() - 1800000,
                isRead = true,
                isSent = true,
                isDelivered = true
            ),
            Message(
                messageId = UUID.randomUUID().toString(),
                conversationId = conversationId,
                senderId = "leim001",
                receiverId = "current_user",
                groupId = null,
                content = "支持 **Markdown** 格式哦！\n\n- 列表项 1\n- 列表项 2\n\n`代码块`也支持",
                timestamp = System.currentTimeMillis() - 900000,
                isRead = false,
                isSent = true,
                isDelivered = true
            )
        )
        insertMessages(mockMessages)
    }
}