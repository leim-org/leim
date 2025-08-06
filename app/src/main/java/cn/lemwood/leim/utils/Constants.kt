package cn.lemwood.leim.utils

/**
 * 应用常量定义
 */
object Constants {
    
    // WebSocket相关常量
    object WebSocket {
        const val DEFAULT_SERVER_URL = "ws://localhost:8080/websocket"
        const val CONNECT_TIMEOUT = 10000L // 10秒
        const val RECONNECT_INTERVAL = 5000L // 5秒
        const val MAX_RECONNECT_ATTEMPTS = 5
        const val HEARTBEAT_INTERVAL = 30000L // 30秒
        const val HEARTBEAT_TIMEOUT = 10000L // 10秒
    }
    
    // 消息类型常量
    object MessageType {
        const val TEXT = "text"
        const val IMAGE = "image"
        const val FILE = "file"
        const val AUDIO = "audio"
        const val VIDEO = "video"
        const val LOCATION = "location"
        const val SYSTEM = "system"
        const val HEARTBEAT = "heartbeat"
        const val AUTH = "auth"
        const val USER_ONLINE = "user_online"
        const val USER_OFFLINE = "user_offline"
        const val MESSAGE_READ = "message_read"
        const val MESSAGE_DELIVERED = "message_delivered"
        const val TYPING_START = "typing_start"
        const val TYPING_STOP = "typing_stop"
        const val FRIEND_REQUEST = "friend_request"
        const val FRIEND_ACCEPT = "friend_accept"
        const val FRIEND_REJECT = "friend_reject"
    }
    
    // 消息状态常量
    object MessageStatus {
        const val SENDING = 0
        const val SENT = 1
        const val DELIVERED = 2
        const val READ = 3
        const val FAILED = 4
    }
    
    // 对话类型常量
    object ConversationType {
        const val PRIVATE = "private"
        const val GROUP = "group"
        const val SYSTEM = "system"
    }
    
    // 联系人类型常量
    object ContactType {
        const val FRIEND = "friend"
        const val GROUP = "group"
        const val STRANGER = "stranger"
        const val BLOCKED = "blocked"
    }
    
    // 用户状态常量
    object UserStatus {
        const val ONLINE = "online"
        const val OFFLINE = "offline"
        const val AWAY = "away"
        const val BUSY = "busy"
        const val INVISIBLE = "invisible"
    }
    
    // 通知相关常量
    object Notification {
        const val CHANNEL_ID = "leim_messages"
        const val CHANNEL_NAME = "消息通知"
        const val CHANNEL_DESCRIPTION = "接收新消息通知"
        const val MESSAGE_NOTIFICATION_ID = 1001
        const val FRIEND_REQUEST_NOTIFICATION_ID = 1002
        const val SYSTEM_NOTIFICATION_ID = 1003
        const val WEBSOCKET_SERVICE_NOTIFICATION_ID = 1004
    }
    
    // 文件相关常量
    object File {
        const val MAX_IMAGE_SIZE = 10 * 1024 * 1024 // 10MB
        const val MAX_FILE_SIZE = 100 * 1024 * 1024 // 100MB
        const val MAX_AUDIO_DURATION = 60 * 1000 // 60秒
        const val MAX_VIDEO_DURATION = 5 * 60 * 1000 // 5分钟
        const val CACHE_DIR = "leim_cache"
        const val DOWNLOAD_DIR = "leim_downloads"
        const val AVATAR_DIR = "avatars"
        const val TEMP_DIR = "temp"
    }
    
    // 数据库相关常量
    object Database {
        const val NAME = "leim_database"
        const val VERSION = 1
        const val BACKUP_DIR = "database_backup"
    }
    
    // SharedPreferences相关常量
    object Preferences {
        const val NAME = "leim_preferences"
        const val USER_ID = "user_id"
        const val USER_TOKEN = "user_token"
        const val USER_INFO = "user_info"
        const val SERVER_URL = "server_url"
        const val AUTO_START = "auto_start"
        const val NOTIFICATION_ENABLED = "notification_enabled"
        const val SOUND_ENABLED = "sound_enabled"
        const val VIBRATION_ENABLED = "vibration_enabled"
        const val DARK_MODE = "dark_mode"
        const val FONT_SIZE = "font_size"
        const val LANGUAGE = "language"
        const val LAST_LOGIN_TIME = "last_login_time"
        const val FIRST_LAUNCH = "first_launch"
        const val IS_LOGGED_IN = "is_logged_in"
        const val CURRENT_USER = "current_user"
    }
    
    // 网络相关常量
    object Network {
        const val CONNECT_TIMEOUT = 30000L // 30秒
        const val READ_TIMEOUT = 30000L // 30秒
        const val WRITE_TIMEOUT = 30000L // 30秒
        const val RETRY_COUNT = 3
        const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
    }
    
    // UI相关常量
    object UI {
        const val ANIMATION_DURATION = 300L
        const val SPLASH_DELAY = 2000L
        const val TYPING_TIMEOUT = 3000L // 3秒
        const val SEARCH_DELAY = 500L // 搜索延迟
        const val REFRESH_THRESHOLD = 100 // 下拉刷新阈值
        const val LOAD_MORE_THRESHOLD = 5 // 加载更多阈值
    }
    
    // 权限相关常量
    object Permission {
        const val REQUEST_CAMERA = 1001
        const val REQUEST_STORAGE = 1002
        const val REQUEST_AUDIO = 1003
        const val REQUEST_LOCATION = 1004
        const val REQUEST_NOTIFICATION = 1005
        const val REQUEST_PHONE = 1006
        const val REQUEST_CONTACTS = 1007
    }
    
    // Intent相关常量
    object Intent {
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_CONVERSATION_ID = "extra_conversation_id"
        const val EXTRA_CONTACT_ID = "extra_contact_id"
        const val EXTRA_MESSAGE_ID = "extra_message_id"
        const val EXTRA_GROUP_ID = "extra_group_id"
        const val EXTRA_FILE_PATH = "extra_file_path"
        const val EXTRA_IMAGE_URL = "extra_image_url"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_CONTENT = "extra_content"
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_DATA = "extra_data"
    }
    
    // 错误码常量
    object ErrorCode {
        const val NETWORK_ERROR = 1001
        const val SERVER_ERROR = 1002
        const val AUTH_ERROR = 1003
        const val PERMISSION_ERROR = 1004
        const val FILE_ERROR = 1005
        const val DATABASE_ERROR = 1006
        const val WEBSOCKET_ERROR = 1007
        const val UNKNOWN_ERROR = 9999
    }
    
    // API相关常量
    object Api {
        const val BASE_URL = "https://api.lemwood.cn/"
        const val VERSION = "v1"
        const val LOGIN = "auth/login"
        const val REGISTER = "auth/register"
        const val LOGOUT = "auth/logout"
        const val REFRESH_TOKEN = "auth/refresh"
        const val USER_INFO = "user/info"
        const val UPDATE_PROFILE = "user/profile"
        const val UPLOAD_AVATAR = "user/avatar"
        const val CONTACTS = "contacts"
        const val CONVERSATIONS = "conversations"
        const val MESSAGES = "messages"
        const val GROUPS = "groups"
        const val FILES = "files"
    }
    
    // 正则表达式常量
    object Regex {
        const val EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        const val PHONE = "^1[3-9]\\d{9}$"
        const val LEIM_ID = "^[a-zA-Z0-9_]{6,20}$"
        const val PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$"
        const val USERNAME = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]{2,20}$"
        const val GROUP_NAME = "^[\\u4e00-\\u9fa5a-zA-Z0-9_\\s]{2,30}$"
    }
    
    // 时间格式常量
    object TimeFormat {
        const val FULL_DATE_TIME = "yyyy-MM-dd HH:mm:ss"
        const val DATE_TIME = "MM-dd HH:mm"
        const val TIME_ONLY = "HH:mm"
        const val DATE_ONLY = "yyyy-MM-dd"
        const val MONTH_DAY = "MM-dd"
        const val YEAR_MONTH = "yyyy-MM"
        const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    
    // 默认值常量
    object Default {
        const val PAGE_SIZE = 20
        const val AVATAR_SIZE = 200
        const val THUMBNAIL_SIZE = 100
        const val MAX_MESSAGE_LENGTH = 5000
        const val MAX_NICKNAME_LENGTH = 20
        const val MAX_SIGNATURE_LENGTH = 100
        const val MAX_GROUP_MEMBERS = 500
        const val MIN_PASSWORD_LENGTH = 8
        const val MAX_PASSWORD_LENGTH = 20
    }
    
    // 兼容性常量（保持向后兼容）
    @Deprecated("使用 WebSocket.DEFAULT_SERVER_URL", ReplaceWith("WebSocket.DEFAULT_SERVER_URL"))
    const val DEFAULT_WEBSOCKET_URL = WebSocket.DEFAULT_SERVER_URL
    
    @Deprecated("使用 WebSocket.RECONNECT_INTERVAL", ReplaceWith("WebSocket.RECONNECT_INTERVAL"))
    const val WEBSOCKET_RECONNECT_INTERVAL = WebSocket.RECONNECT_INTERVAL
    
    @Deprecated("使用 WebSocket.MAX_RECONNECT_ATTEMPTS", ReplaceWith("WebSocket.MAX_RECONNECT_ATTEMPTS"))
    const val WEBSOCKET_MAX_RECONNECT_ATTEMPTS = WebSocket.MAX_RECONNECT_ATTEMPTS
    
    @Deprecated("使用 Notification.CHANNEL_ID", ReplaceWith("Notification.CHANNEL_ID"))
    const val NOTIFICATION_CHANNEL_ID = Notification.CHANNEL_ID
    
    @Deprecated("使用 Notification.CHANNEL_NAME", ReplaceWith("Notification.CHANNEL_NAME"))
    const val NOTIFICATION_CHANNEL_NAME = Notification.CHANNEL_NAME
    
    @Deprecated("使用 Notification.WEBSOCKET_SERVICE_NOTIFICATION_ID", ReplaceWith("Notification.WEBSOCKET_SERVICE_NOTIFICATION_ID"))
    const val WEBSOCKET_SERVICE_NOTIFICATION_ID = Notification.WEBSOCKET_SERVICE_NOTIFICATION_ID
    
    @Deprecated("使用 Notification.MESSAGE_NOTIFICATION_ID", ReplaceWith("Notification.MESSAGE_NOTIFICATION_ID"))
    const val MESSAGE_NOTIFICATION_ID = Notification.MESSAGE_NOTIFICATION_ID
    
    @Deprecated("使用 Preferences.IS_LOGGED_IN", ReplaceWith("Preferences.IS_LOGGED_IN"))
    const val PREF_IS_LOGGED_IN = Preferences.IS_LOGGED_IN
    
    @Deprecated("使用 Preferences.CURRENT_USER", ReplaceWith("Preferences.CURRENT_USER"))
    const val PREF_CURRENT_USER = Preferences.CURRENT_USER
    
    @Deprecated("使用 Preferences.SERVER_URL", ReplaceWith("Preferences.SERVER_URL"))
    const val PREF_SERVER_URL = Preferences.SERVER_URL
    
    @Deprecated("使用 Preferences.AUTO_START", ReplaceWith("Preferences.AUTO_START"))
    const val PREF_AUTO_START = Preferences.AUTO_START
    
    @Deprecated("使用 Preferences.NOTIFICATION_ENABLED", ReplaceWith("Preferences.NOTIFICATION_ENABLED"))
    const val PREF_NOTIFICATION_ENABLED = Preferences.NOTIFICATION_ENABLED
    
    @Deprecated("使用 MessageType.TEXT", ReplaceWith("MessageType.TEXT"))
    const val MESSAGE_TYPE_TEXT = MessageType.TEXT
    
    @Deprecated("使用 MessageType.IMAGE", ReplaceWith("MessageType.IMAGE"))
    const val MESSAGE_TYPE_IMAGE = MessageType.IMAGE
    
    @Deprecated("使用 MessageType.FILE", ReplaceWith("MessageType.FILE"))
    const val MESSAGE_TYPE_FILE = MessageType.FILE
    
    @Deprecated("使用 MessageType.SYSTEM", ReplaceWith("MessageType.SYSTEM"))
    const val MESSAGE_TYPE_SYSTEM = MessageType.SYSTEM
    
    @Deprecated("使用 MessageType.AUTH", ReplaceWith("MessageType.AUTH"))
    const val WS_MESSAGE_TYPE_AUTH = MessageType.AUTH
    
    @Deprecated("使用 MessageType.TEXT", ReplaceWith("MessageType.TEXT"))
    const val WS_MESSAGE_TYPE_CHAT = MessageType.TEXT
    
    @Deprecated("使用 MessageType.HEARTBEAT", ReplaceWith("MessageType.HEARTBEAT"))
    const val WS_MESSAGE_TYPE_HEARTBEAT = MessageType.HEARTBEAT
    
    @Deprecated("使用 MessageType.USER_ONLINE", ReplaceWith("MessageType.USER_ONLINE"))
    const val WS_MESSAGE_TYPE_STATUS = MessageType.USER_ONLINE
    
    @Deprecated("使用 ContactType.FRIEND", ReplaceWith("ContactType.FRIEND"))
    const val CONTACT_TYPE_FRIEND = ContactType.FRIEND
    
    @Deprecated("使用 ContactType.GROUP", ReplaceWith("ContactType.GROUP"))
    const val CONTACT_TYPE_GROUP = ContactType.GROUP
    
    @Deprecated("使用 TimeFormat.FULL_DATE_TIME", ReplaceWith("TimeFormat.FULL_DATE_TIME"))
    const val TIME_FORMAT_FULL = TimeFormat.FULL_DATE_TIME
    
    @Deprecated("使用 TimeFormat.TIME_ONLY", ReplaceWith("TimeFormat.TIME_ONLY"))
    const val TIME_FORMAT_SHORT = TimeFormat.TIME_ONLY
    
    @Deprecated("使用 TimeFormat.MONTH_DAY", ReplaceWith("TimeFormat.MONTH_DAY"))
    const val TIME_FORMAT_DATE = TimeFormat.MONTH_DAY
    
    @Deprecated("使用 File.MAX_FILE_SIZE", ReplaceWith("File.MAX_FILE_SIZE"))
    const val MAX_FILE_SIZE = File.MAX_FILE_SIZE
    
    @Deprecated("使用 Default.AVATAR_SIZE", ReplaceWith("Default.AVATAR_SIZE"))
    const val AVATAR_SIZE = Default.AVATAR_SIZE
}