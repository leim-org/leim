package cn.lemwood.leim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 会话实体类
 */
@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey
    val conversationId: String,   // 会话 ID
    val title: String,            // 会话标题
    val type: String,             // 会话类型：private, group
    val participantIds: String,   // 参与者 ID 列表（JSON 格式）
    val lastMessageId: String?,   // 最后一条消息 ID
    val lastMessageContent: String?, // 最后一条消息内容
    val lastMessageTime: Long = 0L, // 最后一条消息时间
    val unreadCount: Int = 0,     // 未读消息数
    val isArchived: Boolean = false, // 是否归档
    val isMuted: Boolean = false, // 是否静音
    val avatar: String? = null,   // 会话头像
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)