package cn.lemwood.leim.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cn.lemwood.leim.data.model.User

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE leimId = :leimId")
    suspend fun getUserById(leimId: String): User?
    
    @Query("SELECT * FROM users WHERE leimId = :leimId")
    fun getUserByIdLiveData(leimId: String): LiveData<User?>
    
    @Query("SELECT * FROM users ORDER BY nickname ASC")
    fun getAllUsers(): LiveData<List<User>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("DELETE FROM users WHERE leimId = :leimId")
    suspend fun deleteUserById(leimId: String)
    
    @Query("UPDATE users SET isOnline = :isOnline, lastSeen = :lastSeen WHERE leimId = :leimId")
    suspend fun updateUserOnlineStatus(leimId: String, isOnline: Boolean, lastSeen: Long)
}