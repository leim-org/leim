package cn.lemwood.leim.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * 偏好设置管理器
 */
class PreferenceManager(context: Context) {
    
    companion object {
        private const val PREF_NAME = "leim_preferences"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NICKNAME = "user_nickname"
        private const val KEY_AUTO_START_ENABLED = "auto_start_enabled"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    }
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    
    /**
     * 设置登录状态
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    /**
     * 获取用户 ID
     */
    fun getUserId(): String? = sharedPreferences.getString(KEY_USER_ID, null)
    
    /**
     * 设置用户 ID
     */
    fun setUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }
    
    /**
     * 获取用户昵称
     */
    fun getUserNickname(): String? = sharedPreferences.getString(KEY_USER_NICKNAME, null)
    
    /**
     * 设置用户昵称
     */
    fun setUserNickname(nickname: String) {
        sharedPreferences.edit().putString(KEY_USER_NICKNAME, nickname).apply()
    }
    
    /**
     * 检查是否开启自启动
     */
    fun isAutoStartEnabled(): Boolean = sharedPreferences.getBoolean(KEY_AUTO_START_ENABLED, true)
    
    /**
     * 设置自启动开关
     */
    fun setAutoStartEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_AUTO_START_ENABLED, enabled).apply()
    }
    
    /**
     * 检查是否开启通知
     */
    fun isNotificationEnabled(): Boolean = sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true)
    
    /**
     * 设置通知开关
     */
    fun setNotificationEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply()
    }
    
    /**
     * 检查是否开启声音
     */
    fun isSoundEnabled(): Boolean = sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true)
    
    /**
     * 设置声音开关
     */
    fun setSoundEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }
    
    /**
     * 检查是否开启震动
     */
    fun isVibrationEnabled(): Boolean = sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, true)
    
    /**
     * 设置震动开关
     */
    fun setVibrationEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }
    
    /**
     * 清除所有数据（退出登录时使用）
     */
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}