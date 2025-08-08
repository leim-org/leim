package cn.lemwood.leim.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.lemwood.leim.LeimApplication
import cn.lemwood.leim.data.database.entities.User
import cn.lemwood.leim.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * 联系人 ViewModel
 */
class ContactViewModel : ViewModel() {
    
    private val userRepository: UserRepository = LeimApplication.instance.userRepository
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    private val _searchResults = MutableLiveData<List<User>>()
    val searchResults: LiveData<List<User>> = _searchResults
    
    /**
     * 获取所有联系人
     */
    fun getAllContacts(): LiveData<List<User>> {
        return userRepository.getAllContacts()
    }
    
    /**
     * 搜索用户
     */
    fun searchUsers(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val results = userRepository.searchUsers(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _errorMessage.value = "搜索失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 添加联系人
     */
    fun addContact(user: User) {
        viewModelScope.launch {
            try {
                val updatedUser = user.copy(isContact = true)
                userRepository.updateUser(updatedUser)
            } catch (e: Exception) {
                _errorMessage.value = "添加联系人失败: ${e.message}"
            }
        }
    }
    
    /**
     * 删除联系人
     */
    fun removeContact(user: User) {
        viewModelScope.launch {
            try {
                val updatedUser = user.copy(isContact = false)
                userRepository.updateUser(updatedUser)
            } catch (e: Exception) {
                _errorMessage.value = "删除联系人失败: ${e.message}"
            }
        }
    }
    
    /**
     * 添加模拟联系人（用于测试）
     */
    fun addMockContacts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                userRepository.addMockContacts()
            } catch (e: Exception) {
                _errorMessage.value = "添加模拟联系人失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}