package cn.lemwood.leim.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cn.lemwood.leim.data.model.Contact
import cn.lemwood.leim.data.model.ContactType

@Dao
interface ContactDao {
    
    @Query("SELECT * FROM contacts WHERE contactType = :type ORDER BY nickname ASC")
    fun getContactsByType(type: ContactType): LiveData<List<Contact>>
    
    @Query("SELECT * FROM contacts ORDER BY lastMessageTime DESC")
    fun getAllContacts(): LiveData<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE contactId = :contactId")
    suspend fun getContactById(contactId: String): Contact?
    
    @Query("SELECT * FROM contacts WHERE leimId = :leimId")
    suspend fun getContactByLeimId(leimId: String): Contact?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<Contact>)
    
    @Update
    suspend fun updateContact(contact: Contact)
    
    @Delete
    suspend fun deleteContact(contact: Contact)
    
    @Query("DELETE FROM contacts WHERE contactId = :contactId")
    suspend fun deleteContactById(contactId: String)
    
    @Query("UPDATE contacts SET unreadCount = :count WHERE contactId = :contactId")
    suspend fun updateUnreadCount(contactId: String, count: Int)
    
    @Query("UPDATE contacts SET lastMessage = :message, lastMessageTime = :time WHERE contactId = :contactId")
    suspend fun updateLastMessage(contactId: String, message: String, time: Long)
    
    @Query("SELECT * FROM contacts WHERE nickname LIKE '%' || :query || '%' OR leimId LIKE '%' || :query || '%'")
    suspend fun searchContacts(query: String): List<Contact>
}