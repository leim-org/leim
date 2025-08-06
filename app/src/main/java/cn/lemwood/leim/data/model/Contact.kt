package cn.lemwood.leim.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey
    val contactId: String,
    val leimId: String,
    val nickname: String,
    val avatar: String? = null,
    val signature: String? = null,
    val remark: String? = null, // 备注名
    val isBlocked: Boolean = false,
    val isFriend: Boolean = true,
    val addedAt: Long = System.currentTimeMillis(),
    val lastMessageTime: Long = 0,
    val lastMessage: String? = null,
    val unreadCount: Int = 0,
    val contactType: ContactType = ContactType.FRIEND
) : Parcelable

enum class ContactType {
    FRIEND,
    GROUP,
    STRANGER
}