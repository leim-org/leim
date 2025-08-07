package cn.lemwood.leim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 消息实体类
 */
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey
    val messageId: String,        // 消息 ID
    val conversationId: String,   // 会话 ID
    val senderId: String,         // 发送者 Leim 号
    val receiverId: String?,      // 接收者 Leim 号（私聊时使用）
    val groupId: String?,         // 群组 ID（群聊时使用）
    val content: String,          // 消息内容（支持 Markdown）
    val messageType: String = "text", // 消息类型：text, image, file, system
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,  // 是否已读
    val isSent: Boolean = false,  // 是否已发送
    val isDelivered: Boolean = false, // 是否已送达
    val replyToMessageId: String? = null, // 回复的消息 ID
    val fileUrl: String? = null,  // 文件 URL（文件消息时使用）
    val fileName: String? = null, // 文件名
    val fileSize: Long? = null    // 文件大小
)