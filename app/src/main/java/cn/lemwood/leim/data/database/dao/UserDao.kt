package cn.lemwood.leim.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import cn.lemwood.leim.data.database.entities.User

/**
 * 用户数据访问对象
 */
@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUser(): User?
    
    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUserFlow(): Flow<User?>
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
    
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
    
    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<User>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)
    
    @Query("UPDATE users SET isCurrentUser = 0")
    suspend fun clearCurrentUser()
    
    @Query("UPDATE users SET isCurrentUser = 1 WHERE id = :userId")
    suspend fun setCurrentUser(userId: String)
    
    @Query("UPDATE users SET status = :status WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, status: String)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}