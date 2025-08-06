package cn.lemwood.leim.data.repository

import androidx.lifecycle.LiveData
import cn.lemwood.leim.data.dao.MessageDao
import cn.lemwood.leim.data.model.Message

class MessageRepository(private val messageDao: MessageDao) {
    
    fun getMessagesByConversation(conversationId: String): LiveData<List<Message>> = 
        messageDao.getMessagesByConversation(conversationId)
    
    suspend fun getRecentMessages(conversationId: String, limit: Int = 50): List<Message> = 
        messageDao.getRecentMessages(conversationId, limit)
    
    suspend fun getMessageById(messageId: String): Message? = messageDao.getMessageById(messageId)
    
    suspend fun insertMessage(message: Message) = messageDao.insertMessage(message)
    
    suspend fun insertMessages(messages: List<Message>) = messageDao.insertMessages(messages)
    
    suspend fun updateMessage(message: Message) = messageDao.updateMessage(message)
    
    suspend fun deleteMessage(message: Message) = messageDao.deleteMessage(message)
    
    suspend fun deleteMessageById(messageId: String) = messageDao.deleteMessageById(messageId)
    
    suspend fun markMessagesAsRead(conversationId: String, currentUserId: String) = 
        messageDao.markMessagesAsRead(conversationId, currentUserId)
    
    suspend fun getUnreadCount(conversationId: String, currentUserId: String): Int = 
        messageDao.getUnreadCount(conversationId, currentUserId)
    
    suspend fun deleteMessagesByConversation(conversationId: String) = 
        messageDao.deleteMessagesByConversation(conversationId)
}