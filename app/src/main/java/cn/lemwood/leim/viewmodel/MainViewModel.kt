package cn.lemwood.leim.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.lemwood.leim.LeimApplication
import cn.lemwood.leim.data.model.Contact
import cn.lemwood.leim.data.model.Conversation
import cn.lemwood.leim.data.model.User
import cn.lemwood.leim.data.websocket.WebSocketManager
import cn.lemwood.leim.utils.PreferenceManager
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val app = application as LeimApplication
    private val userRepository = app.userRepository
    private val contactRepository = app.contactRepository
    private val preferenceManager = PreferenceManager(application)
    private val webSocketManager = WebSocketManager.getInstance()
    
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    private val _isConnected = MutableLiveData<Boolean>(false)
    val isConnected: LiveData<Boolean> = _isConnected
    
    // 登录状态
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    
    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 错误信息
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // 未读消息数量
    private val _unreadCount = MutableLiveData<Int>(0)
    val unreadCount: LiveData<Int> = _unreadCount
    
    // 网络状态
    private val _networkAvailable = MutableLiveData<Boolean>(true)
    val networkAvailable: LiveData<Boolean> = _networkAvailable
    
    val allContacts: LiveData<List<Contact>> = contactRepository.getAllContacts()
    
    init {
        checkLoginStatus()
        observeWebSocketConnection()
    }
    
    /**
     * 检查登录状态
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val isLoggedIn = preferenceManager.isLoggedIn()
                _isLoggedIn.value = isLoggedIn
                
                if (isLoggedIn) {
                    // 获取当前用户信息
                    val user = userRepository.getCurrentUser()
                    _currentUser.value = user
                    
                    // 如果有用户信息，尝试连接WebSocket
                    user?.let {
                        connectWebSocket()
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "检查登录状态失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 连接WebSocket
     */
    private fun connectWebSocket() {
        viewModelScope.launch {
            try {
                val serverUrl = preferenceManager.getServerUrl()
                if (serverUrl.isNotEmpty()) {
                    webSocketManager.connect(serverUrl)
                }
            } catch (e: Exception) {
                _errorMessage.value = "连接服务器失败: ${e.message}"
            }
        }
    }
    
    /**
     * 观察WebSocket连接状态
     */
    private fun observeWebSocketConnection() {
        viewModelScope.launch {
            webSocketManager.connectionState.collect { isConnected ->
                _isConnected.value = isConnected
            }
        }
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()
                _currentUser.value = user
            } catch (e: Exception) {
                _errorMessage.value = "加载用户信息失败: ${e.message}"
            }
        }
    }
    
    fun updateConnectionStatus(isConnected: Boolean) {
        _isConnected.value = isConnected
    }
    
    fun refreshContacts() {
        viewModelScope.launch {
            try {
                // TODO: 从服务器刷新联系人列表
                // 目前只是重新加载本地数据
            } catch (e: Exception) {
                _errorMessage.value = "刷新联系人失败: ${e.message}"
            }
        }
    }
    
    /**
     * 登录
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // TODO: 实现实际的登录逻辑
                // 目前使用模拟数据
                val user = User(
                    id = "user_${System.currentTimeMillis()}",
                    username = username,
                    nickname = username,
                    email = "$username@leim.cn",
                    avatarUrl = "",
                    status = "online",
                    isCurrentUser = true
                )
                
                // 保存用户信息
                userRepository.insertUser(user)
                userRepository.setCurrentUser(user.id)
                
                // 更新登录状态
                preferenceManager.setLoggedIn(true)
                preferenceManager.setUserInfo(user)
                
                _currentUser.value = user
                _isLoggedIn.value = true
                
                // 连接WebSocket
                connectWebSocket()
                
            } catch (e: Exception) {
                _errorMessage.value = "登录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 注册
     */
    fun register(username: String, password: String, email: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // TODO: 实现实际的注册逻辑
                // 目前使用模拟数据
                val user = User(
                    id = "user_${System.currentTimeMillis()}",
                    username = username,
                    nickname = username,
                    email = email,
                    avatarUrl = "",
                    status = "online",
                    isCurrentUser = true
                )
                
                // 保存用户信息
                userRepository.insertUser(user)
                userRepository.setCurrentUser(user.id)
                
                // 更新登录状态
                preferenceManager.setLoggedIn(true)
                preferenceManager.setUserInfo(user)
                
                _currentUser.value = user
                _isLoggedIn.value = true
                
                // 连接WebSocket
                connectWebSocket()
                
            } catch (e: Exception) {
                _errorMessage.value = "注册失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 登出
     */
    fun logout() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // 断开WebSocket连接
                webSocketManager.disconnect()
                
                // 清除用户数据
                userRepository.clearCurrentUser()
                preferenceManager.setLoggedIn(false)
                preferenceManager.clearUserInfo()
                
                _currentUser.value = null
                _isLoggedIn.value = false
                _isConnected.value = false
                
            } catch (e: Exception) {
                _errorMessage.value = "登出失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 重新连接WebSocket
     */
    fun reconnectWebSocket() {
        viewModelScope.launch {
            try {
                webSocketManager.reconnect()
            } catch (e: Exception) {
                _errorMessage.value = "重连失败: ${e.message}"
            }
        }
    }
    
    /**
     * 更新用户状态
     */
    fun updateUserStatus(status: String) {
        viewModelScope.launch {
            try {
                _currentUser.value?.let { user ->
                    val updatedUser = user.copy(status = status)
                    userRepository.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                    
                    // 通过WebSocket发送状态更新
                    webSocketManager.sendStatusUpdate(status)
                }
            } catch (e: Exception) {
                _errorMessage.value = "更新状态失败: ${e.message}"
            }
        }
    }
    
    /**
     * 更新未读消息数量
     */
    fun updateUnreadCount(count: Int) {
        _unreadCount.value = count
    }
    
    /**
     * 设置网络状态
     */
    fun setNetworkAvailable(available: Boolean) {
        _networkAvailable.value = available
        
        if (available && _isLoggedIn.value == true && _isConnected.value == false) {
            // 网络恢复时尝试重连
            reconnectWebSocket()
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * 刷新用户信息
     */
    fun refreshUserInfo() {
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()
                _currentUser.value = user
            } catch (e: Exception) {
                _errorMessage.value = "刷新用户信息失败: ${e.message}"
            }
        }
    }
}