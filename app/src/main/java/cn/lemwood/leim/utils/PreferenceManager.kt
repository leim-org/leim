package cn.lemwood.leim.utils

import android.content.Context
import android.content.SharedPreferences
import cn.lemwood.leim.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * SharedPreferences管理工具类
 * 用于管理应用的本地配置和用户数据
 */
class PreferenceManager private constructor(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Constants.Preferences.NAME, 
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    companion object {
        @Volatile
        private var INSTANCE: PreferenceManager? = null
        
        fun getInstance(context: Context): PreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferenceManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // 用户相关
    fun setCurrentUser(user: User?) {
        if (user != null) {
            putString(Constants.Preferences.CURRENT_USER, gson.toJson(user))
            putString(Constants.Preferences.USER_ID, user.id)
            putBoolean(Constants.Preferences.IS_LOGGED_IN, true)
        } else {
            remove(Constants.Preferences.CURRENT_USER)
            remove(Constants.Preferences.USER_ID)
            putBoolean(Constants.Preferences.IS_LOGGED_IN, false)
        }
    }
    
    fun getCurrentUser(): User? {
        val userJson = getString(Constants.Preferences.CURRENT_USER)
        return if (userJson.isNotEmpty()) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
    
    fun getCurrentUserId(): String {
        return getString(Constants.Preferences.USER_ID)
    }
    
    fun isLoggedIn(): Boolean {
        return getBoolean(Constants.Preferences.IS_LOGGED_IN, false)
    }
    
    fun setUserToken(token: String) {
        putString(Constants.Preferences.USER_TOKEN, token)
    }
    
    fun getUserToken(): String {
        return getString(Constants.Preferences.USER_TOKEN)
    }
    
    fun setLastLoginTime(timestamp: Long) {
        putLong(Constants.Preferences.LAST_LOGIN_TIME, timestamp)
    }
    
    fun getLastLoginTime(): Long {
        return getLong(Constants.Preferences.LAST_LOGIN_TIME, 0L)
    }
    
    // 应用设置
    fun setServerUrl(url: String) {
        putString(Constants.Preferences.SERVER_URL, url)
    }
    
    fun getServerUrl(): String {
        return getString(Constants.Preferences.SERVER_URL, Constants.WebSocket.DEFAULT_SERVER_URL)
    }
    
    fun setAutoStart(enabled: Boolean) {
        putBoolean(Constants.Preferences.AUTO_START, enabled)
    }
    
    fun isAutoStartEnabled(): Boolean {
        return getBoolean(Constants.Preferences.AUTO_START, true)
    }
    
    fun setNotificationEnabled(enabled: Boolean) {
        putBoolean(Constants.Preferences.NOTIFICATION_ENABLED, enabled)
    }
    
    fun isNotificationEnabled(): Boolean {
        return getBoolean(Constants.Preferences.NOTIFICATION_ENABLED, true)
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        putBoolean(Constants.Preferences.SOUND_ENABLED, enabled)
    }
    
    fun isSoundEnabled(): Boolean {
        return getBoolean(Constants.Preferences.SOUND_ENABLED, true)
    }
    
    fun setVibrationEnabled(enabled: Boolean) {
        putBoolean(Constants.Preferences.VIBRATION_ENABLED, enabled)
    }
    
    fun isVibrationEnabled(): Boolean {
        return getBoolean(Constants.Preferences.VIBRATION_ENABLED, true)
    }
    
    fun setDarkMode(enabled: Boolean) {
        putBoolean(Constants.Preferences.DARK_MODE, enabled)
    }
    
    fun isDarkModeEnabled(): Boolean {
        return getBoolean(Constants.Preferences.DARK_MODE, false)
    }
    
    fun setFontSize(size: Int) {
        putInt(Constants.Preferences.FONT_SIZE, size)
    }
    
    fun getFontSize(): Int {
        return getInt(Constants.Preferences.FONT_SIZE, 14)
    }
    
    fun setLanguage(language: String) {
        putString(Constants.Preferences.LANGUAGE, language)
    }
    
    fun getLanguage(): String {
        return getString(Constants.Preferences.LANGUAGE, "zh")
    }
    
    fun setFirstLaunch(isFirst: Boolean) {
        putBoolean(Constants.Preferences.FIRST_LAUNCH, isFirst)
    }
    
    fun isFirstLaunch(): Boolean {
        return getBoolean(Constants.Preferences.FIRST_LAUNCH, true)
    }
    
    // 通用方法
    private fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
    
    private fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
    
    private fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }
    
    private fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
    
    private fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }
    
    private fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }
    
    private fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }
    
    private fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }
    
    private fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
    
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
    
    // 兼容性方法（保持向后兼容）
    @Deprecated("使用 setCurrentUser", ReplaceWith("setCurrentUser(user)"))
    fun saveUserInfo(user: User) {
        setCurrentUser(user)
    }
    
    @Deprecated("使用 getCurrentUser", ReplaceWith("getCurrentUser()"))
    fun getUserInfo(): User? {
        return getCurrentUser()
    }
    
    @Deprecated("使用 setCurrentUser(null)", ReplaceWith("setCurrentUser(null)"))
    fun clearUserInfo() {
        setCurrentUser(null)
    }
    
    @Deprecated("使用 isLoggedIn", ReplaceWith("isLoggedIn()"))
    fun setLoggedIn(isLoggedIn: Boolean) {
        putBoolean(Constants.Preferences.IS_LOGGED_IN, isLoggedIn)
    }
    
    @Deprecated("使用 setAutoStart", ReplaceWith("setAutoStart(enabled)"))
    fun setAutoStartEnabled(enabled: Boolean) {
        setAutoStart(enabled)
    }
    
    // 登出清理
    fun logout() {
        val serverUrl = getServerUrl()
        val autoStart = isAutoStartEnabled()
        val notificationEnabled = isNotificationEnabled()
        val soundEnabled = isSoundEnabled()
        val vibrationEnabled = isVibrationEnabled()
        val darkMode = isDarkModeEnabled()
        val fontSize = getFontSize()
        val language = getLanguage()
        
        clear()
        
        // 保留应用设置
        setServerUrl(serverUrl)
        setAutoStart(autoStart)
        setNotificationEnabled(notificationEnabled)
        setSoundEnabled(soundEnabled)
        setVibrationEnabled(vibrationEnabled)
        setDarkMode(darkMode)
        setFontSize(fontSize)
        setLanguage(language)
        setFirstLaunch(false)
    }
}