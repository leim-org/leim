package cn.lemwood.leim.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import cn.lemwood.leim.data.database.entities.Contact

/**
 * 联系人数据访问对象
 */
@Dao
interface ContactDao {
    
    @Query("SELECT * FROM contacts WHERE type = 'friend' ORDER BY nickname ASC")
    suspend fun getAllFriends(): List<Contact>
    
    @Query("SELECT * FROM contacts WHERE type = 'friend' ORDER BY nickname ASC")
    fun getAllFriendsFlow(): Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE type = 'group' ORDER BY nickname ASC")
    suspend fun getAllGroups(): List<Contact>
    
    @Query("SELECT * FROM contacts WHERE type = 'group' ORDER BY nickname ASC")
    fun getAllGroupsFlow(): Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts ORDER BY nickname ASC")
    suspend fun getAllContacts(): List<Contact>
    
    @Query("SELECT * FROM contacts ORDER BY nickname ASC")
    fun getAllContactsFlow(): Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE id = :contactId")
    suspend fun getContactById(contactId: String): Contact?
    
    @Query("SELECT * FROM contacts WHERE userId = :userId")
    suspend fun getContactByUserId(userId: String): Contact?
    
    @Query("SELECT * FROM contacts WHERE username = :username")
    suspend fun getContactByUsername(username: String): Contact?
    
    @Query("SELECT * FROM contacts WHERE isFavorite = 1 ORDER BY nickname ASC")
    suspend fun getFavoriteContacts(): List<Contact>
    
    @Query("SELECT * FROM contacts WHERE isFavorite = 1 ORDER BY nickname ASC")
    fun getFavoriteContactsFlow(): Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE nickname LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    suspend fun searchContacts(query: String): List<Contact>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<Contact>)
    
    @Update
    suspend fun updateContact(contact: Contact)
    
    @Delete
    suspend fun deleteContact(contact: Contact)
    
    @Query("DELETE FROM contacts WHERE id = :contactId")
    suspend fun deleteContactById(contactId: String)
    
    @Query("UPDATE contacts SET status = :status WHERE userId = :userId")
    suspend fun updateContactStatus(userId: String, status: String)
    
    @Query("UPDATE contacts SET isFavorite = :isFavorite WHERE id = :contactId")
    suspend fun updateContactFavorite(contactId: String, isFavorite: Boolean)
    
    @Query("UPDATE contacts SET isBlocked = :isBlocked WHERE id = :contactId")
    suspend fun updateContactBlocked(contactId: String, isBlocked: Boolean)
    
    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()
}