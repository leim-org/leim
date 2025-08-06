package cn.lemwood.leim.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import cn.lemwood.leim.data.database.entities.Message

/**
 * 消息数据访问对象
 */
@Dao
interface MessageDao {
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    suspend fun getMessagesByConversation(conversationId: String): List<Message>
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesByConversationFlow(conversationId: String): Flow<List<Message>>
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMessageByConversation(conversationId: String): Message?
    
    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): Message?
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND isRead = 0")
    suspend fun getUnreadMessages(conversationId: String): List<Message>
    
    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId AND isRead = 0")
    suspend fun getUnreadMessageCount(conversationId: String): Int
    
    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId AND isRead = 0")
    fun getUnreadMessageCountFlow(conversationId: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)
    
    @Update
    suspend fun updateMessage(message: Message)
    
    @Delete
    suspend fun deleteMessage(message: Message)
    
    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: String)
    
    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversation(conversationId: String)
    
    @Query("UPDATE messages SET isRead = 1 WHERE conversationId = :conversationId AND isRead = 0")
    suspend fun markMessagesAsRead(conversationId: String)
    
    @Query("UPDATE messages SET isSent = 1 WHERE id = :messageId")
    suspend fun markMessageAsSent(messageId: String)
    
    @Query("UPDATE messages SET isDelivered = 1 WHERE id = :messageId")
    suspend fun markMessageAsDelivered(messageId: String)
    
    @Query("SELECT DISTINCT conversationId FROM messages ORDER BY timestamp DESC")
    suspend fun getAllConversationIds(): List<String>
    
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}