package cn.lemwood.leim.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey
    val conversationId: String,
    val title: String,
    val avatar: String? = null,
    val conversationType: ConversationType,
    val lastMessageId: String? = null,
    val lastMessageContent: String? = null,
    val lastMessageTime: Long = 0,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

enum class ConversationType {
    PRIVATE, // 私聊
    GROUP    // 群聊
}