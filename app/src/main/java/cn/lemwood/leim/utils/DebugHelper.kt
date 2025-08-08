package cn.lemwood.leim.utils

import android.content.Context
import android.util.Log

/**
 * 调试助手类
 */
object DebugHelper {
    
    private const val TAG = "DebugHelper"
    
    /**
     * 检查应用基础状态
     */
    fun checkAppStatus(context: Context): String {
        val status = StringBuilder()
        
        try {
            status.append("=== 应用状态检查 ===\n")
            
            // 检查PreferenceManager
            val preferenceManager = PreferenceManager(context)
            val isLoggedIn = preferenceManager.isLoggedIn()
            val userId = preferenceManager.getUserId()
            val nickname = preferenceManager.getUserNickname()
            
            status.append("登录状态: $isLoggedIn\n")
            status.append("用户ID: $userId\n")
            status.append("用户昵称: $nickname\n")
            
            // 检查权限状态
            val hasAllPermissions = PermissionHelper.hasAllPermissions(context)
            status.append("权限状态: $hasAllPermissions\n")
            
            // 检查崩溃日志
            val crashLogs = CrashLogManager.getCrashLogs(context)
            status.append("崩溃日志数量: ${crashLogs.size}\n")
            
            if (crashLogs.isNotEmpty()) {
                status.append("最新崩溃: ${crashLogs.first().timestamp}\n")
            }
            
            status.append("=== 检查完成 ===\n")
            
        } catch (e: Exception) {
            status.append("检查过程中发生错误: ${e.message}\n")
            Log.e(TAG, "检查应用状态失败", e)
        }
        
        val result = status.toString()
        Log.d(TAG, result)
        return result
    }
    
    /**
     * 清除所有用户数据（用于测试）
     */
    fun clearAllData(context: Context) {
        try {
            Log.d(TAG, "清除所有用户数据")
            val preferenceManager = PreferenceManager(context)
            preferenceManager.setLoggedIn(false)
            preferenceManager.setUserId("")
            preferenceManager.setUserNickname("")
            
            // 清除崩溃日志
            CrashLogManager.clearAllLogs(context)
            
            Log.d(TAG, "数据清除完成")
        } catch (e: Exception) {
            Log.e(TAG, "清除数据失败", e)
        }
    }
}