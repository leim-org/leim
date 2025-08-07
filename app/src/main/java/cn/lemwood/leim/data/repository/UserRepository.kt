package cn.lemwood.leim.data.repository

import androidx.lifecycle.LiveData
import cn.lemwood.leim.data.database.dao.UserDao
import cn.lemwood.leim.data.database.entities.User

/**
 * 用户数据仓库
 */
class UserRepository(private val userDao: UserDao) {
    
    fun getAllContacts(): LiveData<List<User>> = userDao.getAllContacts()
    
    suspend fun getUserById(leimId: String): User? = userDao.getUserById(leimId)
    
    fun getUserByIdLiveData(leimId: String): LiveData<User?> = userDao.getUserByIdLiveData(leimId)
    
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    
    suspend fun insertUsers(users: List<User>) = userDao.insertUsers(users)
    
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    
    suspend fun updateUserStatus(leimId: String, status: String, lastSeen: Long) = 
        userDao.updateUserStatus(leimId, status, lastSeen)
    
    suspend fun updateContactStatus(leimId: String, isContact: Boolean) = 
        userDao.updateContactStatus(leimId, isContact)
    
    suspend fun searchUsers(query: String): List<User> = userDao.searchUsers(query)
    
    /**
     * 添加模拟联系人数据
     */
    suspend fun addMockContacts() {
        val mockUsers = listOf(
            User(
                leimId = "leim001",
                nickname = "张三",
                avatar = null,
                status = "online",
                isContact = true
            ),
            User(
                leimId = "leim002", 
                nickname = "李四",
                avatar = null,
                status = "offline",
                isContact = true
            ),
            User(
                leimId = "leim003",
                nickname = "王五",
                avatar = null,
                status = "busy",
                isContact = true
            )
        )
        insertUsers(mockUsers)
    }
}