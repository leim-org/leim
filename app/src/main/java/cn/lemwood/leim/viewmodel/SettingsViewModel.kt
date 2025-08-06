package cn.lemwood.leim.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.lemwood.leim.LeimApplication
import cn.lemwood.leim.data.model.User
import cn.lemwood.leim.data.repository.UserRepository
import cn.lemwood.leim.data.websocket.WebSocketManager
import cn.lemwood.leim.utils.PreferenceManager
import kotlinx.coroutines.launch

/**
 * 设置页面ViewModel
 * 管理用户设置和应用配置
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val app = application as LeimApplication
    private val userRepository = app.userRepository
    private val preferenceManager = PreferenceManager(application)
    private val webSocketManager = WebSocketManager.getInstance()
    
    // 当前用户
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    // 服务器地址
    private val _serverUrl = MutableLiveData<String>()
    val serverUrl: LiveData<String> = _serverUrl
    
    // 自启动设置
    private val _autoStart = MutableLiveData<Boolean>()
    val autoStart: LiveData<Boolean> = _autoStart
    
    // 通知设置
    private val _notificationEnabled = MutableLiveData<Boolean>()
    val notificationEnabled: LiveData<Boolean> = _notificationEnabled
    
    // 声音设置
    private val _soundEnabled = MutableLiveData<Boolean>()
    val soundEnabled: LiveData<Boolean> = _soundEnabled
    
    // 震动设置
    private val _vibrationEnabled = MutableLiveData<Boolean>()
    val vibrationEnabled: LiveData<Boolean> = _vibrationEnabled
    
    // 主题设置
    private val _darkMode = MutableLiveData<Boolean>()
    val darkMode: LiveData<Boolean> = _darkMode
    
    // 字体大小
    private val _fontSize = MutableLiveData<Int>()
    val fontSize: LiveData<Int> = _fontSize
    
    // 语言设置
    private val _language = MutableLiveData<String>()
    val language: LiveData<String> = _language
    
    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 错误信息
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // 保存状态
    private val _isSaving = MutableLiveData<Boolean>(false)
    val isSaving: LiveData<Boolean> = _isSaving
    
    init {
        loadSettings()
        loadCurrentUser()
    }
    
    /**
     * 加载设置
     */
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // 加载各种设置
                _serverUrl.value = preferenceManager.getServerUrl()
                _autoStart.value = preferenceManager.isAutoStartEnabled()
                _notificationEnabled.value = preferenceManager.isNotificationEnabled()
                _soundEnabled.value = preferenceManager.isSoundEnabled()
                _vibrationEnabled.value = preferenceManager.isVibrationEnabled()
                _darkMode.value = preferenceManager.isDarkModeEnabled()
                _fontSize.value = preferenceManager.getFontSize()
                _language.value = preferenceManager.getLanguage()
                
            } catch (e: Exception) {
                _errorMessage.value = "加载设置失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 加载当前用户信息
     */
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
    
    /**
     * 更新服务器地址
     */
    fun updateServerUrl(url: String) {
        viewModelScope.launch {
            try {
                _isSaving.value = true
                preferenceManager.setServerUrl(url)
                _serverUrl.value = url
                
                // 如果WebSocket已连接，需要重新连接
                if (webSocketManager.isConnected()) {
                    webSocketManager.disconnect()
                    webSocketManager.connect(url)
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "更新服务器地址失败: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
    
    /**
     * 更新自启动设置
     */
    fun updateAutoStart(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferenceManager.setAutoStartEnabled(enabled)
                _autoStart.value = enabled
            } catch (e: Exception) {
                _errorMessage.value = "更新自启动设置失败: ${e.message}"
            }
        }
    }
    
    /**
     * 更新通知设置
     */
    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferenceManager.setNotificationEnabled(enabled)
                _notificationEnabled.value = enabled
            } catch (e: Exception) {
                _errorMessage.value = "更新通知设置失败: ${e.message}"
            }
        }
    }
    
    /**
     * 更新声音设置
     */
    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferenceManager.setSoundEnabled(enabled)
                _soundEnabled.value = enabled
            } catch (e: Exception) {
                _errorMessage.value = "更新声音设置失败: ${e.message}"
            }
        }
    }
    
    /**
     * 更新震动设置
     */
    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferenceManager.setVibrationEnabled(enabled)
                _vibrationEnabled.value = enabled
            } catch (e: Exception) {
                _errorMessage.value = "更新震动设置失败: ${e.message}"
            }
        }
    }
    
    /**
     * 更新主题设置
     */
    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferenceManager.setDarkModeEnabled(enabled)
                _darkMode.value = enabled
            } catch (e: Exception) {
                _errorMessage.value = "更新主题设置失败: ${e.message}"
            }
        }
    }
    
    /**
     * 更新字体大小
     */
    fun updateFontSize(size: Int) {
        viewModelScope.launch {
            try {
                preferenceManager.setFontSize(size)
                _fontSize.value = size
            } catch (e: Exception) {
                _errorMessage.value = "更新字体大小失败: ${e.message}"
            }
        }
    }
    
    /**
     * 更新语言设置
     */
    fun updateLanguage(language: String) {
        viewModelScope.launch {
            try {
                preferenceManager.setLanguage(language)
                _language.value = language
            } catch (e: Exception) {
                _errorMessage.value = "更新语言设置失败: ${e.message}"
            }
        }
    }
    
    /**
     * 更新用户信息
     */
    fun updateUserInfo(nickname: String, email: String, signature: String) {
        viewModelScope.launch {
            try {
                _isSaving.value = true
                
                val currentUser = _currentUser.value ?: return@launch
                val updatedUser = currentUser.copy(
                    nickname = nickname,
                    email = email,
                    signature = signature
                )
                
                userRepository.updateUser(updatedUser)
                preferenceManager.setUserInfo(updatedUser)
                _currentUser.value = updatedUser
                
            } catch (e: Exception) {
                _errorMessage.value = "更新用户信息失败: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
    
    /**
     * 更新用户头像
     */
    fun updateUserAvatar(avatarUrl: String) {
        viewModelScope.launch {
            try {
                _isSaving.value = true
                
                val currentUser = _currentUser.value ?: return@launch
                val updatedUser = currentUser.copy(avatarUrl = avatarUrl)
                
                userRepository.updateUser(updatedUser)
                preferenceManager.setUserInfo(updatedUser)
                _currentUser.value = updatedUser
                
            } catch (e: Exception) {
                _errorMessage.value = "更新头像失败: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
    
    /**
     * 修改密码
     */
    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                _isSaving.value = true
                
                // TODO: 实现修改密码逻辑
                // 1. 验证旧密码
                // 2. 发送修改密码请求到服务器
                // 3. 更新本地存储
                
            } catch (e: Exception) {
                _errorMessage.value = "修改密码失败: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
    
    /**
     * 清除缓存
     */
    fun clearCache() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // TODO: 实现清除缓存逻辑
                // 1. 清除图片缓存
                // 2. 清除文件缓存
                // 3. 清除临时数据
                
            } catch (e: Exception) {
                _errorMessage.value = "清除缓存失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 导出聊天记录
     */
    fun exportChatHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // TODO: 实现导出聊天记录逻辑
                // 1. 从数据库读取所有消息
                // 2. 格式化为可读格式
                // 3. 保存到文件
                
            } catch (e: Exception) {
                _errorMessage.value = "导出聊天记录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 检查更新
     */
    fun checkForUpdates() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // TODO: 实现检查更新逻辑
                // 1. 请求服务器获取最新版本信息
                // 2. 比较版本号
                // 3. 提示用户更新
                
            } catch (e: Exception) {
                _errorMessage.value = "检查更新失败: ${e.message}"
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
                
            } catch (e: Exception) {
                _errorMessage.value = "登出失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * 重置所有设置
     */
    fun resetAllSettings() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // 重置为默认值
                preferenceManager.setServerUrl("")
                preferenceManager.setAutoStartEnabled(false)
                preferenceManager.setNotificationEnabled(true)
                preferenceManager.setSoundEnabled(true)
                preferenceManager.setVibrationEnabled(true)
                preferenceManager.setDarkModeEnabled(false)
                preferenceManager.setFontSize(14)
                preferenceManager.setLanguage("zh")
                
                // 重新加载设置
                loadSettings()
                
            } catch (e: Exception) {
                _errorMessage.value = "重置设置失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}