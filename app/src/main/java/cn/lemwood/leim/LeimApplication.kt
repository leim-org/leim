package cn.lemwood.leim

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import cn.lemwood.leim.data.database.LeimDatabase
import cn.lemwood.leim.data.repository.MessageRepository
import cn.lemwood.leim.data.repository.UserRepository
import cn.lemwood.leim.utils.CrashLogger

/**
 * Leim 应用程序类
 * 负责应用程序的全局初始化
 */
class LeimApplication : Application() {
    
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "leim_messages"
        const val NOTIFICATION_CHANNEL_NAME = "消息通知"
        const val SYSTEM_NOTIFICATION_CHANNEL_ID = "leim_system"
        const val SYSTEM_NOTIFICATION_CHANNEL_NAME = "系统通知"
        const val SERVICE_NOTIFICATION_CHANNEL_ID = "leim_service"
        const val SERVICE_NOTIFICATION_CHANNEL_NAME = "服务通知"
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
        
        // 初始化异常日志捕获器
        CrashLogger.init(this)
        
        // 创建通知渠道
        createNotificationChannel()
    }
    
    /**
     * 创建通知渠道（Android 8.0+）
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // 获取默认通知声音
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            
            // 消息通知渠道
            val messageChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Leim 消息通知"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
                
                // 设置声音
                setSound(defaultSoundUri, AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
                    .build())
                
                // 设置振动模式
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }
            
            // 系统通知渠道
            val systemChannel = NotificationChannel(
                SYSTEM_NOTIFICATION_CHANNEL_ID,
                SYSTEM_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Leim 系统通知"
                enableVibration(false)
                enableLights(false)
                setShowBadge(false)
                setSound(null, null) // 系统通知静音
            }
            
            // 服务通知渠道（前台服务）
            val serviceChannel = NotificationChannel(
                SERVICE_NOTIFICATION_CHANNEL_ID,
                SERVICE_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Leim 后台服务"
                enableVibration(false)
                enableLights(false)
                setShowBadge(false)
                setSound(null, null) // 服务通知静音
            }
            
            notificationManager.createNotificationChannels(listOf(
                messageChannel,
                systemChannel,
                serviceChannel
            ))
        }
    }
}