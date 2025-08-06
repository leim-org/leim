package cn.lemwood.leim.data.database

import android.content.Context
import androidx.room.*
import cn.lemwood.leim.data.dao.*
import cn.lemwood.leim.data.model.*

@Database(
    entities = [
        User::class,
        Message::class,
        Contact::class,
        Conversation::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LeimDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun contactDao(): ContactDao
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
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}