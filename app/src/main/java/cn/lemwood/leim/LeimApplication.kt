package cn.lemwood.leim

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import cn.lemwood.leim.data.database.LeimDatabase
import cn.lemwood.leim.data.repository.MessageRepository
import cn.lemwood.leim.data.repository.UserRepository

/**
 * Leim 应用程序类
 * 负责应用程序的全局初始化
 */
class LeimApplication : Application() {
    
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "leim_messages"
        const val NOTIFICATION_CHANNEL_NAME = "消息通知"
        lateinit var instance: LeimApplication
            private set
    }
    
    // 数据库实例
    val database by lazy { LeimDatabase.getDatabase(this) }
    
    // 仓库实例
    val messageRepository by lazy { MessageRepository(database.messageDao()) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 创建通知渠道
        createNotificationChannel()
    }
    
    /**
     * 创建通知渠道（Android 8.0+）
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Leim 消息通知"
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}