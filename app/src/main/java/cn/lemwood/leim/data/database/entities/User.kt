package cn.lemwood.leim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 用户实体类
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val username: String,
    val nickname: String,
    val email: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val status: String = "offline", // online, offline, away, busy
    val signature: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isCurrentUser: Boolean = false
)