package cn.lemwood.leim.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cn.lemwood.leim.data.model.Conversation

@Dao
interface ConversationDao {
    
    @Query("SELECT * FROM conversations ORDER BY isPinned DESC, lastMessageTime DESC")
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
    
    @Query("DELETE FROM conversations WHERE conversationId = :conversationId")
    suspend fun deleteConversationById(conversationId: String)
    
    @Query("UPDATE conversations SET unreadCount = :count WHERE conversationId = :conversationId")
    suspend fun updateUnreadCount(conversationId: String, count: Int)
    
    @Query("UPDATE conversations SET lastMessageContent = :content, lastMessageTime = :time, updatedAt = :updatedAt WHERE conversationId = :conversationId")
    suspend fun updateLastMessage(conversationId: String, content: String, time: Long, updatedAt: Long)
    
    @Query("UPDATE conversations SET isPinned = :isPinned WHERE conversationId = :conversationId")
    suspend fun updatePinnedStatus(conversationId: String, isPinned: Boolean)
    
    @Query("UPDATE conversations SET isMuted = :isMuted WHERE conversationId = :conversationId")
    suspend fun updateMutedStatus(conversationId: String, isMuted: Boolean)
}