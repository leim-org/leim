package cn.lemwood.leim.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.lemwood.leim.data.database.dao.ConversationDao
import cn.lemwood.leim.data.database.dao.MessageDao
import cn.lemwood.leim.data.database.dao.UserDao
import cn.lemwood.leim.data.database.entities.Conversation
import cn.lemwood.leim.data.database.entities.Message
import cn.lemwood.leim.data.database.entities.User

/**
 * Leim 数据库
 */
@Database(
    entities = [User::class, Message::class, Conversation::class],
    version = 1,
    exportSchema = false
)
abstract class LeimDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
    
    companion object {
        @Volatile
        private var INSTANCE: LeimDatabase? = null
        
        fun getDatabase(context: Context): LeimDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LeimDatabase::class.java,
                    "leim_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}