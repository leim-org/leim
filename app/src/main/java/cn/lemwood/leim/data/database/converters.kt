package cn.lemwood.leim.data.database

import androidx.room.TypeConverter
import cn.lemwood.leim.data.model.MessageType
import cn.lemwood.leim.data.model.ContactType
import cn.lemwood.leim.data.model.ConversationType
import java.util.Date

class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromMessageType(type: MessageType): String = type.name
    
    @TypeConverter
    fun toMessageType(type: String): MessageType = MessageType.valueOf(type)
    
    @TypeConverter
    fun fromContactType(type: ContactType): String = type.name
    
    @TypeConverter
    fun toContactType(type: String): ContactType = ContactType.valueOf(type)
    
    @TypeConverter
    fun fromConversationType(type: ConversationType): String = type.name
    
    @TypeConverter
    fun toConversationType(type: String): ConversationType = ConversationType.valueOf(type)
}