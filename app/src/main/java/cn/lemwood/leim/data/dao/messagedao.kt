package cn.lemwood.leim.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cn.lemwood.leim.data.model.Message

@Dao
interface MessageDao {
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesByConversation(conversationId: String): LiveData<List<Message>>
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(conversationId: String, limit: Int = 50): List<Message>
    
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
    
    @Query("DELETE FROM messages WHERE messageId = :messageId")
    suspend fun deleteMessageById(messageId: String)
    
    @Query("UPDATE messages SET isRead = 1 WHERE conversationId = :conversationId AND senderId != :currentUserId")
    suspend fun markMessagesAsRead(conversationId: String, currentUserId: String)
    
    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId AND isRead = 0 AND senderId != :currentUserId")
    suspend fun getUnreadCount(conversationId: String, currentUserId: String): Int
    
    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversation(conversationId: String)
}