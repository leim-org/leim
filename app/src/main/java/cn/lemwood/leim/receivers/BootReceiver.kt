package cn.lemwood.leim.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import cn.lemwood.leim.services.WebSocketService
import cn.lemwood.leim.utils.PreferenceManager

/**
 * 开机自启动接收器
 */
class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "收到广播: ${intent.action}")
        
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                // 检查是否已登录且开启自启动
                val preferenceManager = PreferenceManager(context)
                val isLoggedIn = preferenceManager.isLoggedIn()
                val autoStartEnabled = preferenceManager.isAutoStartEnabled()
                
                if (isLoggedIn && autoStartEnabled) {
                    Log.d(TAG, "启动 WebSocket 服务")
                    val serviceIntent = Intent(context, WebSocketService::class.java)
                    
                    // API 级别兼容性检查
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                } else {
                    Log.d(TAG, "用户未登录或未开启自启动，跳过服务启动")
                }
            }
        }
    }
}