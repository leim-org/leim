package cn.lemwood.leim.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val leimId: String,
    val nickname: String,
    val avatar: String? = null,
    val signature: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable