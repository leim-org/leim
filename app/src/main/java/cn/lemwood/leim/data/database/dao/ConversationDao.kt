package cn.lemwood.leim.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cn.lemwood.leim.data.database.entities.Conversation

/**
 * 会话数据访问对象
 */
@Dao
interface ConversationDao {
    
    @Query("SELECT * FROM conversations WHERE isArchived = 0 ORDER BY lastMessageTime DESC")
    fun getAllConversations(): LiveData<List<Conversation>>
    
    @Query("SELECT * FROM conversations WHERE conversationId = :conversationId")
    suspend fun getConversationById(conversationId: String): Conversation?
    
    @Query("SELECT * FROM conversations WHERE conversationId = :conversationId")
    fun getConversationByIdLiveData(conversationId: String): LiveData<Conversation?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<Conversation>)
    
    @Update
    suspend fun updateConversation(conversation: Conversation)
    
    @Delete
    suspend fun deleteConversation(conversation: Conversation)
    
    @Query("UPDATE conversations SET unreadCount = :count WHERE conversationId = :conversationId")
    suspend fun updateUnreadCount(conversationId: String, count: Int)
    
    @Query("UPDATE conversations SET unreadCount = 0 WHERE conversationId = :conversationId")
    suspend fun clearUnreadCount(conversationId: String)
    
    @Query("UPDATE conversations SET lastMessageId = :messageId, lastMessageContent = :content, lastMessageTime = :time WHERE conversationId = :conversationId")
    suspend fun updateLastMessage(conversationId: String, messageId: String, content: String, time: Long)
    
    @Query("UPDATE conversations SET isArchived = :archived WHERE conversationId = :conversationId")
    suspend fun updateArchiveStatus(conversationId: String, archived: Boolean)
    
    @Query("UPDATE conversations SET isMuted = :muted WHERE conversationId = :conversationId")
    suspend fun updateMuteStatus(conversationId: String, muted: Boolean)
    
    @Query("SELECT SUM(unreadCount) FROM conversations WHERE isMuted = 0")
    fun getTotalUnreadCount(): LiveData<Int>
}