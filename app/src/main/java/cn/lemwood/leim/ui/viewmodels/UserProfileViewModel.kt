package cn.lemwood.leim.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.lemwood.leim.LeimApplication
import cn.lemwood.leim.data.database.entities.User
import cn.lemwood.leim.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * 用户主页 ViewModel
 */
class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userRepository: UserRepository = 
        (application as LeimApplication).database.userDao().let { UserRepository(it) }
    
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _contactStatusChanged = MutableLiveData<Boolean>()
    val contactStatusChanged: LiveData<Boolean> = _contactStatusChanged
    
    /**
     * 加载用户资料
     */
    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val user = userRepository.getUserByLeimId(userId)
                if (user != null) {
                    _user.value = user
                } else {
                    _error.value = "用户不存在"
                }
            } catch (e: Exception) {
                _error.value = "加载用户信息失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 切换联系人状态
     */
    fun toggleContactStatus(userId: String) {
        viewModelScope.launch {
            try {
                val currentUser = _user.value
                if (currentUser != null) {
                    val newStatus = !currentUser.isContact
                    userRepository.updateContactStatus(userId, newStatus)
                    
                    // 更新本地用户信息
                    _user.value = currentUser.copy(isContact = newStatus)
                    _contactStatusChanged.value = newStatus
                }
            } catch (e: Exception) {
                _error.value = "更新联系人状态失败: ${e.message}"
            }
        }
    }
    
    /**
     * 更新用户状态
     */
    fun updateUserStatus(userId: String, status: String) {
        viewModelScope.launch {
            try {
                userRepository.updateUserStatus(userId, status, System.currentTimeMillis())
                
                // 重新加载用户信息
                loadUserProfile(userId)
            } catch (e: Exception) {
                _error.value = "更新用户状态失败: ${e.message}"
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _error.value = null
    }
}