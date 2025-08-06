package cn.lemwood.leim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 联系人实体类
 */
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey
    val id: String,
    val userId: String,
    val username: String,
    val nickname: String,
    val avatarUrl: String? = null,
    val type: String = "friend", // friend, group
    val status: String = "offline", // online, offline, away, busy
    val lastSeen: Date? = null,
    val isBlocked: Boolean = false,
    val isFavorite: Boolean = false,
    val remark: String? = null,
    val groupDescription: String? = null,
    val memberCount: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)