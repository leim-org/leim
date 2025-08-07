package cn.lemwood.leim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户实体类
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val leimId: String,           // Leim 号
    val nickname: String,         // 昵称
    val avatar: String? = null,   // 头像 URL
    val status: String = "offline", // 在线状态：online, offline, busy
    val lastSeen: Long = 0L,      // 最后在线时间
    val isContact: Boolean = false, // 是否为联系人
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)