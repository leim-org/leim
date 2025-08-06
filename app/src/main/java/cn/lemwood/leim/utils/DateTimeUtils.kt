package cn.lemwood.leim.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 日期时间工具类
 * 用于处理时间格式化、转换等操作
 */
object DateTimeUtils {
    
    private val locale = Locale.getDefault()
    
    // 时间格式化器
    private val fullDateTimeFormat = SimpleDateFormat(Constants.TimeFormat.FULL_DATE_TIME, locale)
    private val dateTimeFormat = SimpleDateFormat(Constants.TimeFormat.DATE_TIME, locale)
    private val timeOnlyFormat = SimpleDateFormat(Constants.TimeFormat.TIME_ONLY, locale)
    private val dateOnlyFormat = SimpleDateFormat(Constants.TimeFormat.DATE_ONLY, locale)
    private val monthDayFormat = SimpleDateFormat(Constants.TimeFormat.MONTH_DAY, locale)
    private val yearMonthFormat = SimpleDateFormat(Constants.TimeFormat.YEAR_MONTH, locale)
    private val iso8601Format = SimpleDateFormat(Constants.TimeFormat.ISO_8601, locale).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    /**
     * 获取当前时间戳（毫秒）
     */
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()
    
    /**
     * 获取当前时间戳（秒）
     */
    fun getCurrentTimestampSeconds(): Long = System.currentTimeMillis() / 1000
    
    /**
     * 将时间戳转换为Date对象
     */
    fun timestampToDate(timestamp: Long): Date = Date(timestamp)
    
    /**
     * 将Date对象转换为时间戳
     */
    fun dateToTimestamp(date: Date): Long = date.time
    
    /**
     * 格式化时间戳为完整日期时间字符串
     */
    fun formatFullDateTime(timestamp: Long): String {
        return fullDateTimeFormat.format(Date(timestamp))
    }
    
    /**
     * 格式化时间戳为日期时间字符串（不含年份）
     */
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }
    
    /**
     * 格式化时间戳为时间字符串
     */
    fun formatTimeOnly(timestamp: Long): String {
        return timeOnlyFormat.format(Date(timestamp))
    }
    
    /**
     * 格式化时间戳为日期字符串
     */
    fun formatDateOnly(timestamp: Long): String {
        return dateOnlyFormat.format(Date(timestamp))
    }
    
    /**
     * 格式化时间戳为月日字符串
     */
    fun formatMonthDay(timestamp: Long): String {
        return monthDayFormat.format(Date(timestamp))
    }
    
    /**
     * 格式化时间戳为年月字符串
     */
    fun formatYearMonth(timestamp: Long): String {
        return yearMonthFormat.format(Date(timestamp))
    }
    
    /**
     * 格式化时间戳为ISO 8601格式
     */
    fun formatISO8601(timestamp: Long): String {
        return iso8601Format.format(Date(timestamp))
    }
    
    /**
     * 解析ISO 8601格式的时间字符串
     */
    fun parseISO8601(dateString: String): Long? {
        return try {
            iso8601Format.parse(dateString)?.time
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 智能格式化时间显示
     * 根据时间差显示不同格式：
     * - 今天：显示时间
     * - 昨天：显示"昨天 时间"
     * - 本周：显示"星期X 时间"
     * - 本年：显示"月-日 时间"
     * - 其他：显示"年-月-日 时间"
     */
    fun formatSmartTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        val messageCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        // 今天
        if (isSameDay(now, timestamp)) {
            return formatTimeOnly(timestamp)
        }
        
        // 昨天
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        if (isSameDay(calendar.timeInMillis, timestamp)) {
            return "昨天 ${formatTimeOnly(timestamp)}"
        }
        
        // 本周
        calendar.timeInMillis = now
        val weekStart = calendar.apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        if (timestamp >= weekStart) {
            val dayOfWeek = when (messageCalendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "周一"
                Calendar.TUESDAY -> "周二"
                Calendar.WEDNESDAY -> "周三"
                Calendar.THURSDAY -> "周四"
                Calendar.FRIDAY -> "周五"
                Calendar.SATURDAY -> "周六"
                Calendar.SUNDAY -> "周日"
                else -> ""
            }
            return "$dayOfWeek ${formatTimeOnly(timestamp)}"
        }
        
        // 本年
        calendar.timeInMillis = now
        if (calendar.get(Calendar.YEAR) == messageCalendar.get(Calendar.YEAR)) {
            return "${formatMonthDay(timestamp)} ${formatTimeOnly(timestamp)}"
        }
        
        // 其他年份
        return formatDateTime(timestamp)
    }
    
    /**
     * 格式化消息列表时间显示
     * 更简洁的时间显示格式
     */
    fun formatMessageListTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        
        // 今天显示时间
        if (isSameDay(now, timestamp)) {
            return formatTimeOnly(timestamp)
        }
        
        // 昨天显示"昨天"
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        if (isSameDay(calendar.timeInMillis, timestamp)) {
            return "昨天"
        }
        
        // 本年显示月日
        calendar.timeInMillis = now
        val messageCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        if (calendar.get(Calendar.YEAR) == messageCalendar.get(Calendar.YEAR)) {
            return formatMonthDay(timestamp)
        }
        
        // 其他年份显示年月日
        return formatDateOnly(timestamp)
    }
    
    /**
     * 计算时间差（人性化显示）
     */
    fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "刚刚"
            diff < TimeUnit.HOURS.toMillis(1) -> "${diff / TimeUnit.MINUTES.toMillis(1)}分钟前"
            diff < TimeUnit.DAYS.toMillis(1) -> "${diff / TimeUnit.HOURS.toMillis(1)}小时前"
            diff < TimeUnit.DAYS.toMillis(7) -> "${diff / TimeUnit.DAYS.toMillis(1)}天前"
            diff < TimeUnit.DAYS.toMillis(30) -> "${diff / TimeUnit.DAYS.toMillis(7)}周前"
            diff < TimeUnit.DAYS.toMillis(365) -> "${diff / TimeUnit.DAYS.toMillis(30)}个月前"
            else -> "${diff / TimeUnit.DAYS.toMillis(365)}年前"
        }
    }
    
    /**
     * 判断两个时间戳是否为同一天
     */
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val calendar1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val calendar2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
        
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }
    
    /**
     * 判断时间戳是否为今天
     */
    fun isToday(timestamp: Long): Boolean {
        return isSameDay(System.currentTimeMillis(), timestamp)
    }
    
    /**
     * 判断时间戳是否为昨天
     */
    fun isYesterday(timestamp: Long): Boolean {
        val yesterday = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
        return isSameDay(yesterday, timestamp)
    }
    
    /**
     * 判断时间戳是否为本周
     */
    fun isThisWeek(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val weekStart = calendar.apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        return timestamp >= weekStart
    }
    
    /**
     * 判断时间戳是否为本年
     */
    fun isThisYear(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val messageCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        return calendar.get(Calendar.YEAR) == messageCalendar.get(Calendar.YEAR)
    }
    
    /**
     * 获取一天的开始时间戳
     */
    fun getDayStart(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    /**
     * 获取一天的结束时间戳
     */
    fun getDayEnd(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }
    
    /**
     * 获取本周开始时间戳
     */
    fun getWeekStart(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    /**
     * 获取本月开始时间戳
     */
    fun getMonthStart(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    /**
     * 获取本年开始时间戳
     */
    fun getYearStart(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}