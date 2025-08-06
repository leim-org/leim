package cn.lemwood.leim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 消息实体类
 */
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val type: String = "text", // text, image, file, system
    val timestamp: Date = Date(),
    val isRead: Boolean = false,
    val isSent: Boolean = false,
    val isDelivered: Boolean = false,
    val replyToMessageId: String? = null,
    val fileUrl: String? = null,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val thumbnailUrl: String? = null
)