package cn.lemwood.leim.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import cn.lemwood.leim.LeimApplication
import cn.lemwood.leim.R
import cn.lemwood.leim.ui.activities.MainActivity

/**
 * 通知助手类
 * 负责处理应用的所有通知功能，包括声音和振动
 */
class NotificationHelper(private val context: Context) {
    
    companion object {
        private const val TAG = "NotificationHelper"
        
        // 振动模式
        private val VIBRATION_PATTERN = longArrayOf(0, 300, 200, 300) // 停止, 振动, 停止, 振动
        private const val VIBRATION_DURATION = 500L // 单次振动时长
    }
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val preferenceManager = PreferenceManager(context)
    private val vibrator = getVibrator()
    
    /**
     * 获取振动器实例
     */
    private fun getVibrator(): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    
    /**
     * 显示消息通知
     */
    fun showMessageNotification(
        title: String,
        content: String,
        conversationId: String? = null,
        senderId: String? = null
    ) {
        if (!preferenceManager.isNotificationEnabled()) {
            return
        }
        
        val intent = Intent(context, MainActivity::class.java).apply {
            conversationId?.let { putExtra("conversation_id", it) }
            senderId?.let { putExtra("sender_id", it) }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            System.currentTimeMillis().toInt(),
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notificationBuilder = NotificationCompat.Builder(context, LeimApplication.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        
        // 设置声音
        if (preferenceManager.isSoundEnabled()) {
            val soundUri = getNotificationSoundUri()
            notificationBuilder.setSound(soundUri)
        } else {
            notificationBuilder.setSound(null)
        }
        
        // 设置振动
        if (preferenceManager.isVibrationEnabled()) {
            notificationBuilder.setVibrate(VIBRATION_PATTERN)
        } else {
            notificationBuilder.setVibrate(null)
        }
        
        val notification = notificationBuilder.build()
        notificationManager.notify(generateNotificationId(), notification)
        
        // 手动触发振动（如果启用）
        if (preferenceManager.isVibrationEnabled()) {
            triggerVibration()
        }
    }
    
    /**
     * 显示系统通知
     */
    fun showSystemNotification(title: String, content: String) {
        if (!preferenceManager.isNotificationEnabled()) {
            return
        }
        
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, LeimApplication.SYSTEM_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()
        
        notificationManager.notify(generateNotificationId(), notification)
    }
    
    /**
     * 创建前台服务通知
     */
    fun createForegroundNotification(): android.app.Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(context, LeimApplication.SERVICE_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Leim")
            .setContentText("正在后台运行")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setSound(null) // 前台服务通知不需要声音
            .setVibrate(null) // 前台服务通知不需要振动
            .build()
    }
    
    /**
     * 手动触发振动
     */
    fun triggerVibration() {
        if (!preferenceManager.isVibrationEnabled()) {
            return
        }
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createWaveform(VIBRATION_PATTERN, -1)
                vibrator.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(VIBRATION_PATTERN, -1)
            }
        } catch (e: Exception) {
            // 忽略振动错误，某些设备可能不支持振动
        }
    }
    
    /**
     * 手动播放通知声音
     */
    fun playNotificationSound() {
        if (!preferenceManager.isSoundEnabled()) {
            return
        }
        
        try {
            val soundUri = getNotificationSoundUri()
            val ringtone = RingtoneManager.getRingtone(context, soundUri)
            ringtone?.play()
        } catch (e: Exception) {
            // 忽略声音播放错误
        }
    }
    
    /**
     * 获取通知声音 URI
     */
    private fun getNotificationSoundUri(): Uri {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ?: Uri.EMPTY
    }
    
    /**
     * 生成唯一的通知 ID
     */
    private fun generateNotificationId(): Int {
        return System.currentTimeMillis().toInt()
    }
    
    /**
     * 取消所有通知
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
    
    /**
     * 取消指定通知
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}