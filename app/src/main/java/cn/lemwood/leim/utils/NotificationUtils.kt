package cn.lemwood.leim.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import cn.lemwood.leim.R
import cn.lemwood.leim.data.database.entities.Message
import cn.lemwood.leim.ui.activities.MainActivity

/**
 * 通知工具类
 * 处理应用内的各种通知功能
 */
object NotificationUtils {
    
    /**
     * 初始化通知渠道
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // 消息通知渠道
            val messageChannel = NotificationChannel(
                Constants.Notification.CHANNEL_ID_MESSAGE,
                Constants.Notification.CHANNEL_NAME_MESSAGE,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "新消息通知"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            
            // 系统通知渠道
            val systemChannel = NotificationChannel(
                Constants.Notification.CHANNEL_ID_SYSTEM,
                Constants.Notification.CHANNEL_NAME_SYSTEM,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "系统通知"
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
            }
            
            // 服务通知渠道
            val serviceChannel = NotificationChannel(
                Constants.Notification.CHANNEL_ID_SERVICE,
                Constants.Notification.CHANNEL_NAME_SERVICE,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "后台服务通知"
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
            }
            
            // 好友请求通知渠道
            val friendRequestChannel = NotificationChannel(
                Constants.Notification.CHANNEL_ID_FRIEND_REQUEST,
                Constants.Notification.CHANNEL_NAME_FRIEND_REQUEST,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "好友请求通知"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            
            notificationManager.createNotificationChannels(listOf(
                messageChannel,
                systemChannel,
                serviceChannel,
                friendRequestChannel
            ))
        }
    }
    
    /**
     * 创建通知渠道（向后兼容）
     */
    @Deprecated("使用 createNotificationChannels() 替代", ReplaceWith("createNotificationChannels(context)"))
    fun createNotificationChannel(context: Context) {
        createNotificationChannels(context)
    }
    
    /**
     * 显示新消息通知
     */
    fun showMessageNotification(
        context: Context,
        message: Message,
        senderName: String,
        conversationName: String? = null,
        unreadCount: Int = 1
    ) {
        if (!hasNotificationPermission(context) || !isNotificationEnabled(context)) {
            return
        }
        
        val notificationId = message.conversationId.hashCode()
        
        // 创建点击意图
        val clickIntent = createMainActivityIntent(context, message.conversationId)
        val clickPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 创建回复意图
        val replyPendingIntent = createReplyPendingIntent(context, message.conversationId, notificationId)
        val replyAction = createReplyAction(context, replyPendingIntent)
        
        // 创建标记已读意图
        val markReadPendingIntent = createMarkReadPendingIntent(context, message.conversationId, notificationId)
        val markReadAction = NotificationCompat.Action.Builder(
            R.drawable.ic_mark_read,
            "标记已读",
            markReadPendingIntent
        ).build()
        
        // 获取头像
        val largeIcon = getContactAvatar(context, message.senderId)
        
        // 构建通知
        val builder = NotificationCompat.Builder(context, Constants.Notification.CHANNEL_ID_MESSAGE)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setContentTitle(conversationName ?: senderName)
            .setContentText(getMessagePreview(message))
            .setSubText(if (unreadCount > 1) "${unreadCount}条新消息" else null)
            .setNumber(unreadCount)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(clickPendingIntent)
            .addAction(replyAction)
            .addAction(markReadAction)
            .setGroup(Constants.Notification.GROUP_MESSAGE)
            .setGroupSummary(false)
        
        // 设置声音和震动
        if (isSoundEnabled(context)) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }
        if (isVibrationEnabled(context)) {
            builder.setVibrate(longArrayOf(0, 300, 200, 300))
        }
        
        // 设置消息样式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val person = Person.Builder()
                .setName(senderName)
                .setIcon(IconCompat.createWithBitmap(largeIcon))
                .build()
            
            val messagingStyle = NotificationCompat.MessagingStyle(person)
                .setConversationTitle(conversationName)
                .addMessage(getMessagePreview(message), message.timestamp, person)
            
            builder.setStyle(messagingStyle)
        }
        
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // 权限被拒绝，忽略通知
        }
    }
    
    /**
     * 显示消息汇总通知
     */
    fun showMessageSummaryNotification(
        context: Context,
        totalUnreadCount: Int,
        conversationCount: Int
    ) {
        if (!hasNotificationPermission(context) || totalUnreadCount <= 1) {
            return
        }
        
        val clickIntent = createMainActivityIntent(context)
        val clickPendingIntent = PendingIntent.getActivity(
            context,
            Constants.Notification.ID_MESSAGE_SUMMARY,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val builder = NotificationCompat.Builder(context, Constants.Notification.CHANNEL_ID_MESSAGE)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Leim")
            .setContentText("${totalUnreadCount}条新消息来自${conversationCount}个对话")
            .setNumber(totalUnreadCount)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(clickPendingIntent)
            .setGroup(Constants.Notification.GROUP_MESSAGE)
            .setGroupSummary(true)
        
        try {
            NotificationManagerCompat.from(context).notify(
                Constants.Notification.ID_MESSAGE_SUMMARY,
                builder.build()
            )
        } catch (e: SecurityException) {
            // 权限被拒绝，忽略通知
        }
    }
    
    /**
     * 显示好友请求通知
     */
    fun showFriendRequestNotification(
        context: Context,
        requestId: String,
        senderName: String,
        message: String?
    ) {
        if (!hasNotificationPermission(context)) {
            return
        }
        
        val notificationId = requestId.hashCode()
        
        // 创建接受意图
        val acceptPendingIntent = createAcceptFriendPendingIntent(context, requestId, notificationId)
        val acceptAction = NotificationCompat.Action.Builder(
            R.drawable.ic_check,
            "接受",
            acceptPendingIntent
        ).build()
        
        // 创建拒绝意图
        val rejectPendingIntent = createRejectFriendPendingIntent(context, requestId, notificationId)
        val rejectAction = NotificationCompat.Action.Builder(
            R.drawable.ic_close,
            "拒绝",
            rejectPendingIntent
        ).build()
        
        val clickIntent = createMainActivityIntent(context, null, "friend_requests")
        val clickPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val builder = NotificationCompat.Builder(context, Constants.Notification.CHANNEL_ID_FRIEND_REQUEST)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("新的好友请求")
            .setContentText("${senderName}${if (message.isNullOrBlank()) "请求添加您为好友" else ": $message"}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setAutoCancel(true)
            .setContentIntent(clickPendingIntent)
            .addAction(acceptAction)
            .addAction(rejectAction)
        
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // 权限被拒绝，忽略通知
        }
    }
    
    /**
     * 显示系统通知
     */
    fun showSystemNotification(
        context: Context,
        title: String,
        content: String,
        notificationId: Int = Constants.Notification.ID_SYSTEM
    ) {
        if (!hasNotificationPermission(context)) {
            return
        }
        
        val clickIntent = createMainActivityIntent(context)
        val clickPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val builder = NotificationCompat.Builder(context, Constants.Notification.CHANNEL_ID_SYSTEM)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(clickPendingIntent)
        
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // 权限被拒绝，忽略通知
        }
    }
    
    /**
     * 显示消息通知（向后兼容）
     */
    @Deprecated("使用 showMessageNotification(Message, String, String?, Int) 替代")
    fun showMessageNotification(
        context: Context,
        title: String,
        content: String,
        conversationId: String? = null
    ) {
        if (!hasNotificationPermission(context)) {
            return
        }
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            conversationId?.let { putExtra("conversation_id", it) }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, Constants.Notification.CHANNEL_ID_MESSAGE)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(Constants.Notification.ID_MESSAGE, notification)
        } catch (e: SecurityException) {
            // 权限被拒绝，忽略通知
        }
    }
    
    /**
     * 创建前台服务通知
     */
    fun createForegroundServiceNotification(
        context: Context,
        title: String = "Leim",
        content: String = "正在后台运行",
        isConnected: Boolean = true
    ): android.app.Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val statusText = if (isConnected) content else "连接已断开"
        val iconRes = if (isConnected) R.drawable.ic_notification else R.drawable.ic_notification_disconnected
        
        return NotificationCompat.Builder(context, Constants.Notification.CHANNEL_ID_SERVICE)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(statusText)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .setShowWhen(false)
            .setContentIntent(pendingIntent)
            .build()
    }
    
    /**
     * 显示WebSocket服务通知（向后兼容）
     */
    @Deprecated("使用 createForegroundServiceNotification() 替代", ReplaceWith("createForegroundServiceNotification(context)"))
    fun showWebSocketServiceNotification(context: Context): android.app.Notification {
        return createForegroundServiceNotification(context)
    }
    
    // ==================== 通知管理方法 ====================
    
    /**
     * 取消指定通知
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        try {
            NotificationManagerCompat.from(context).cancel(notificationId)
        } catch (e: Exception) {
            // 忽略异常
        }
    }
    
    /**
     * 取消对话相关通知
     */
    fun cancelConversationNotifications(context: Context, conversationId: String) {
        val notificationId = conversationId.hashCode()
        cancelNotification(context, notificationId)
    }
    
    /**
     * 取消所有消息通知
     */
    fun cancelAllMessageNotifications(context: Context) {
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            // 取消消息汇总通知
            notificationManager.cancel(Constants.Notification.ID_MESSAGE_SUMMARY)
            // 取消所有消息通知（通过组取消）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notificationManager.cancel(Constants.Notification.GROUP_MESSAGE, Constants.Notification.ID_MESSAGE_SUMMARY)
            }
        } catch (e: Exception) {
            // 忽略异常
        }
    }
    
    /**
     * 取消所有通知
     */
    fun cancelAllNotifications(context: Context) {
        try {
            NotificationManagerCompat.from(context).cancelAll()
        } catch (e: Exception) {
            // 忽略异常
        }
    }
    
    /**
     * 更新通知角标数量
     */
    fun updateNotificationBadge(context: Context, count: Int) {
        // Android 原生不支持角标，这里可以集成第三方库或使用厂商API
        // 例如：ShortcutBadger.applyCount(context, count)
    }
    
    /**
     * 清除通知角标
     */
    fun clearNotificationBadge(context: Context) {
        updateNotificationBadge(context, 0)
    }
    
    // ==================== 权限和设置检查 ====================
    
    /**
     * 检查通知权限
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
    
    /**
     * 检查通知是否启用
     */
    private fun isNotificationEnabled(context: Context): Boolean {
        return PreferenceManager.getInstance(context).isNotificationEnabled()
    }
    
    /**
     * 检查声音是否启用
     */
    private fun isSoundEnabled(context: Context): Boolean {
        return PreferenceManager.getInstance(context).isNotificationSoundEnabled()
    }
    
    /**
     * 检查震动是否启用
     */
    private fun isVibrationEnabled(context: Context): Boolean {
        return PreferenceManager.getInstance(context).isNotificationVibrationEnabled()
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 获取消息预览文本
     */
    private fun getMessagePreview(message: Message): String {
        return when (message.type) {
            Constants.MessageType.TEXT -> message.content
            Constants.MessageType.IMAGE -> "[图片]"
            Constants.MessageType.FILE -> "[文件] ${message.fileName ?: "未知文件"}"
            Constants.MessageType.AUDIO -> "[语音]"
            Constants.MessageType.VIDEO -> "[视频]"
            Constants.MessageType.LOCATION -> "[位置]"
            else -> "[消息]"
        }
    }
    
    /**
     * 获取联系人头像
     */
    private fun getContactAvatar(context: Context, userId: String): Bitmap {
        // 这里应该从缓存或数据库获取用户头像
        // 暂时返回默认头像
        return BitmapFactory.decodeResource(context.resources, R.drawable.ic_default_avatar)
    }
    
    /**
     * 创建主界面意图
     */
    private fun createMainActivityIntent(
        context: Context,
        conversationId: String? = null,
        tab: String? = null
    ): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            conversationId?.let { putExtra(Constants.Intent.EXTRA_CONVERSATION_ID, it) }
            tab?.let { putExtra(Constants.Intent.EXTRA_TAB, it) }
        }
    }
    
    /**
     * 创建回复PendingIntent
     */
    private fun createReplyPendingIntent(
        context: Context,
        conversationId: String,
        requestCode: Int
    ): PendingIntent {
        val replyIntent = Intent(context, NotificationReplyReceiver::class.java).apply {
            putExtra(Constants.Intent.EXTRA_CONVERSATION_ID, conversationId)
            putExtra(Constants.Intent.EXTRA_NOTIFICATION_ID, requestCode)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
    
    /**
     * 创建回复Action
     */
    private fun createReplyAction(context: Context, replyPendingIntent: PendingIntent): NotificationCompat.Action {
        val remoteInput = RemoteInput.Builder(Constants.Intent.EXTRA_REPLY_TEXT)
            .setLabel("回复消息")
            .build()
        
        return NotificationCompat.Action.Builder(
            R.drawable.ic_reply,
            "回复",
            replyPendingIntent
        ).addRemoteInput(remoteInput).build()
    }
    
    /**
     * 创建标记已读PendingIntent
     */
    private fun createMarkReadPendingIntent(
        context: Context,
        conversationId: String,
        requestCode: Int
    ): PendingIntent {
        val markReadIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = Constants.Intent.ACTION_MARK_READ
            putExtra(Constants.Intent.EXTRA_CONVERSATION_ID, conversationId)
            putExtra(Constants.Intent.EXTRA_NOTIFICATION_ID, requestCode)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode + 1000,
            markReadIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    /**
     * 创建接受好友请求PendingIntent
     */
    private fun createAcceptFriendPendingIntent(
        context: Context,
        requestId: String,
        requestCode: Int
    ): PendingIntent {
        val acceptIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = Constants.Intent.ACTION_ACCEPT_FRIEND
            putExtra(Constants.Intent.EXTRA_REQUEST_ID, requestId)
            putExtra(Constants.Intent.EXTRA_NOTIFICATION_ID, requestCode)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode + 2000,
            acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    /**
     * 创建拒绝好友请求PendingIntent
     */
    private fun createRejectFriendPendingIntent(
        context: Context,
        requestId: String,
        requestCode: Int
    ): PendingIntent {
        val rejectIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = Constants.Intent.ACTION_REJECT_FRIEND
            putExtra(Constants.Intent.EXTRA_REQUEST_ID, requestId)
            putExtra(Constants.Intent.EXTRA_NOTIFICATION_ID, requestCode)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode + 3000,
            rejectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
}