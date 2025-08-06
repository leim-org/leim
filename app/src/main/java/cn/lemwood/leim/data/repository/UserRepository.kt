package cn.lemwood.leim.data.repository

import androidx.lifecycle.LiveData
import cn.lemwood.leim.data.dao.UserDao
import cn.lemwood.leim.data.model.User

class UserRepository(private val userDao: UserDao) {
    
    fun getAllUsers(): LiveData<List<User>> = userDao.getAllUsers()
    
    fun getUserById(leimId: String): LiveData<User?> = userDao.getUserByIdLiveData(leimId)
    
    suspend fun getUserByIdSync(leimId: String): User? = userDao.getUserById(leimId)
    
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    
    suspend fun insertUsers(users: List<User>) = userDao.insertUsers(users)
    
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    
    suspend fun deleteUserById(leimId: String) = userDao.deleteUserById(leimId)
    
    suspend fun updateUserOnlineStatus(leimId: String, isOnline: Boolean, lastSeen: Long = System.currentTimeMillis()) {
        userDao.updateUserOnlineStatus(leimId, isOnline, lastSeen)
    }
}