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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * 认证页面ViewModel
 * 管理用户登录、注册和认证状态
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val app = application as LeimApplication
    private val userRepository = app.userRepository
    private val preferenceManager = PreferenceManager(application)
    private val webSocketManager = WebSocketManager.getInstance()
    
    // 登录状态
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    
    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 错误信息
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // 成功信息
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    // 当前用户
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    // 验证码发送状态
    private val _isCodeSent = MutableLiveData<Boolean>(false)
    val isCodeSent: LiveData<Boolean> = _isCodeSent
    
    // 验证码倒计时
    private val _codeCountdown = MutableLiveData<Int>(0)
    val codeCountdown: LiveData<Int> = _codeCountdown
    
    // 注册步骤
    private val _registerStep = MutableLiveData<Int>(1)
    val registerStep: LiveData<Int> = _registerStep
    
    // 密码强度
    private val _passwordStrength = MutableLiveData<Int>(0)
    val passwordStrength: LiveData<Int> = _passwordStrength
    
    init {
        checkLoginStatus()
    }
    
    /**
     * 检查登录状态
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                val isLoggedIn = preferenceManager.isLoggedIn()
                _isLoggedIn.value = isLoggedIn
                
                if (isLoggedIn) {
                    val user = userRepository.getCurrentUser()
                    _currentUser.value = user
                }
            } catch (e: Exception) {
                _errorMessage.value = "检查登录状态失败: ${e.message}"
            }
        }
    }
    
    /**
     * 用户登录
     */
    fun login(username: String, password: String, rememberMe: Boolean = false) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // 输入验证
                if (username.isBlank()) {
                    _errorMessage.value = "请输入用户名"
                    return@launch
                }
                
                if (password.isBlank()) {
                    _errorMessage.value = "请输入密码"
                    return@launch
                }
                
                // 模拟网络延迟
                delay(1000)
                
                // TODO: 实现真实的登录逻辑
                // 1. 发送登录请求到服务器
                // 2. 验证用户名和密码
                // 3. 获取用户信息和token
                
                // 模拟登录成功
                val user = User(
                    id = 1,
                    username = username,
                    nickname = "用户_$username",
                    email = "$username@leim.cn",
                    phone = "",
                    avatarUrl = "",
                    status = "online",
                    signature = "这个人很懒，什么都没留下",
                    createdAt = Date(),
                    updatedAt = Date(),
                    isCurrentUser = true
                )
                
                // 保存用户信息
                userRepository.insertUser(user)
                userRepository.setCurrentUser(user.id)
                preferenceManager.setLoggedIn(true)
                preferenceManager.setUserInfo(user)
                
                if (rememberMe) {
                    // TODO: 保存登录凭证
                }
                
                _currentUser.value = user
                _isLoggedIn.value = true
                _successMessage.value = "登录成功"
                
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
     * 用户注册
     */
    fun register(username: String, password: String, email: String, phone: String, verificationCode: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // 输入验证
                if (username.isBlank()) {
                    _errorMessage.value = "请输入用户名"
                    return@launch
                }
                
                if (password.isBlank()) {
                    _errorMessage.value = "请输入密码"
                    return@launch
                }
                
                if (email.isBlank()) {
                    _errorMessage.value = "请输入邮箱"
                    return@launch
                }
                
                if (verificationCode.isBlank()) {
                    _errorMessage.value = "请输入验证码"
                    return@launch
                }
                
                // 验证密码强度
                if (getPasswordStrength(password) < 2) {
                    _errorMessage.value = "密码强度太弱，请使用更复杂的密码"
                    return@launch
                }
                
                // 模拟网络延迟
                delay(1500)
                
                // TODO: 实现真实的注册逻辑
                // 1. 验证验证码
                // 2. 检查用户名是否已存在
                // 3. 发送注册请求到服务器
                // 4. 创建用户账号
                
                // 模拟注册成功
                val user = User(
                    id = System.currentTimeMillis().toInt(),
                    username = username,
                    nickname = username,
                    email = email,
                    phone = phone,
                    avatarUrl = "",
                    status = "online",
                    signature = "新用户",
                    createdAt = Date(),
                    updatedAt = Date(),
                    isCurrentUser = true
                )
                
                // 保存用户信息
                userRepository.insertUser(user)
                userRepository.setCurrentUser(user.id)
                preferenceManager.setLoggedIn(true)
                preferenceManager.setUserInfo(user)
                
                _currentUser.value = user
                _isLoggedIn.value = true
                _successMessage.value = "注册成功"
                
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
     * 发送验证码
     */
    fun sendVerificationCode(email: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                if (email.isBlank()) {
                    _errorMessage.value = "请输入邮箱地址"
                    return@launch
                }
                
                if (!isValidEmail(email)) {
                    _errorMessage.value = "请输入有效的邮箱地址"
                    return@launch
                }
                
                // 模拟网络延迟
                delay(1000)
                
                // TODO: 实现真实的发送验证码逻辑
                // 1. 发送请求到服务器
                // 2. 服务器发送验证码到邮箱
                
                _isCodeSent.value = true
                _successMessage.value = "验证码已发送到您的邮箱"
                
                // 开始倒计时
                startCodeCountdown()
                
            } catch (e: Exception) {
                _errorMessage.value = "发送验证码失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 重置密码
     */
    fun resetPassword(email: String, verificationCode: String, newPassword: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                if (email.isBlank()) {
                    _errorMessage.value = "请输入邮箱地址"
                    return@launch
                }
                
                if (verificationCode.isBlank()) {
                    _errorMessage.value = "请输入验证码"
                    return@launch
                }
                
                if (newPassword.isBlank()) {
                    _errorMessage.value = "请输入新密码"
                    return@launch
                }
                
                // 验证密码强度
                if (getPasswordStrength(newPassword) < 2) {
                    _errorMessage.value = "密码强度太弱，请使用更复杂的密码"
                    return@launch
                }
                
                // 模拟网络延迟
                delay(1000)
                
                // TODO: 实现真实的重置密码逻辑
                // 1. 验证验证码
                // 2. 发送重置密码请求到服务器
                // 3. 更新用户密码
                
                _successMessage.value = "密码重置成功，请使用新密码登录"
                
            } catch (e: Exception) {
                _errorMessage.value = "重置密码失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 用户登出
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
                _successMessage.value = "已退出登录"
                
            } catch (e: Exception) {
                _errorMessage.value = "退出登录失败: ${e.message}"
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
                if (serverUrl.isNotBlank()) {
                    webSocketManager.connect(serverUrl)
                }
            } catch (e: Exception) {
                // WebSocket连接失败不影响登录
            }
        }
    }
    
    /**
     * 开始验证码倒计时
     */
    private fun startCodeCountdown() {
        viewModelScope.launch {
            for (i in 60 downTo 0) {
                _codeCountdown.value = i
                delay(1000)
            }
            _isCodeSent.value = false
        }
    }
    
    /**
     * 验证邮箱格式
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * 验证用户名格式
     */
    fun isValidUsername(username: String): Boolean {
        return username.length >= 3 && username.matches(Regex("^[a-zA-Z0-9_]+$"))
    }
    
    /**
     * 获取密码强度
     * 0: 弱, 1: 中等, 2: 强
     */
    fun getPasswordStrength(password: String): Int {
        var strength = 0
        
        if (password.length >= 8) strength++
        if (password.any { it.isUpperCase() }) strength++
        if (password.any { it.isLowerCase() }) strength++
        if (password.any { it.isDigit() }) strength++
        if (password.any { !it.isLetterOrDigit() }) strength++
        
        return when {
            strength <= 2 -> 0
            strength <= 3 -> 1
            else -> 2
        }
    }
    
    /**
     * 更新密码强度
     */
    fun updatePasswordStrength(password: String) {
        _passwordStrength.value = getPasswordStrength(password)
    }
    
    /**
     * 设置注册步骤
     */
    fun setRegisterStep(step: Int) {
        _registerStep.value = step
    }
    
    /**
     * 下一步注册
     */
    fun nextRegisterStep() {
        val currentStep = _registerStep.value ?: 1
        if (currentStep < 3) {
            _registerStep.value = currentStep + 1
        }
    }
    
    /**
     * 上一步注册
     */
    fun previousRegisterStep() {
        val currentStep = _registerStep.value ?: 1
        if (currentStep > 1) {
            _registerStep.value = currentStep - 1
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * 清除成功信息
     */
    fun clearSuccess() {
        _successMessage.value = null
    }
    
    /**
     * 清除所有消息
     */
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}