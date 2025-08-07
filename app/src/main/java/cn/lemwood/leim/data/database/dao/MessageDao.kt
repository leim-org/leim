package cn.lemwood.leim.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cn.lemwood.leim.data.database.entities.Message

/**
 * 消息数据访问对象
 */
@Dao
interface MessageDao {
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesByConversation(conversationId: String): LiveData<List<Message>>
    
    @Query("SELECT * FROM messages WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: String): Message?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)
    
    @Update
    suspend fun updateMessage(message: Message)
    
    @Delete
    suspend fun deleteMessage(message: Message)
    
    @Query("UPDATE messages SET isRead = 1 WHERE conversationId = :conversationId AND isRead = 0")
    suspend fun markMessagesAsRead(conversationId: String)
    
    @Query("UPDATE messages SET isSent = 1 WHERE messageId = :messageId")
    suspend fun markMessageAsSent(messageId: String)
    
    @Query("UPDATE messages SET isDelivered = 1 WHERE messageId = :messageId")
    suspend fun markMessageAsDelivered(messageId: String)
    
    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId AND isRead = 0")
    suspend fun getUnreadCount(conversationId: String): Int
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMessage(conversationId: String): Message?
    
    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversation(conversationId: String)
}