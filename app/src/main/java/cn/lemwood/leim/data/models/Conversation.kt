package cn.lemwood.leim.data.models

import java.util.Date

/**
 * 对话数据模型
 * 用于在UI层展示对话列表
 */
data class Conversation(
    val id: String,
    val title: String,
    val lastMessage: String,
    val lastMessageTime: Date,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val avatarUrl: String? = null,
    val isGroup: Boolean = false,
    val participantCount: Int = 0
)