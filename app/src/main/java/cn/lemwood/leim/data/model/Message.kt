package cn.lemwood.leim.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey
    val messageId: String,
    val conversationId: String, // 会话ID（可以是用户ID或群组ID）
    val senderId: String,
    val senderNickname: String,
    val content: String,
    val messageType: MessageType = MessageType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isSent: Boolean = false,
    val isDelivered: Boolean = false,
    val replyToMessageId: String? = null,
    val attachmentUrl: String? = null,
    val attachmentType: String? = null
) : Parcelable

enum class MessageType {
    TEXT,
    IMAGE,
    FILE,
    VOICE,
    VIDEO,
    SYSTEM
}