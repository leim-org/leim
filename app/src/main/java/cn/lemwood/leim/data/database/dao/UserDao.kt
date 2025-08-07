package cn.lemwood.leim.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cn.lemwood.leim.data.database.entities.User

/**
 * 用户数据访问对象
 */
@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE isContact = 1 ORDER BY nickname ASC")
    fun getAllContacts(): LiveData<List<User>>
    
    @Query("SELECT * FROM users WHERE leimId = :leimId")
    suspend fun getUserById(leimId: String): User?
    
    @Query("SELECT * FROM users WHERE leimId = :leimId")
    fun getUserByIdLiveData(leimId: String): LiveData<User?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("UPDATE users SET status = :status, lastSeen = :lastSeen WHERE leimId = :leimId")
    suspend fun updateUserStatus(leimId: String, status: String, lastSeen: Long)
    
    @Query("UPDATE users SET isContact = :isContact WHERE leimId = :leimId")
    suspend fun updateContactStatus(leimId: String, isContact: Boolean)
    
    @Query("SELECT * FROM users WHERE nickname LIKE '%' || :query || '%' OR leimId LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<User>
}