package cn.lemwood.leim.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间工具类
 * 用于格式化时间显示
 */
object TimeUtils {
    
    private val timeFormat = SimpleDateFormat(Constants.TIME_FORMAT_SHORT, Locale.getDefault())
    private val dateFormat = SimpleDateFormat(Constants.TIME_FORMAT_DATE, Locale.getDefault())
    private val fullFormat = SimpleDateFormat(Constants.TIME_FORMAT_FULL, Locale.getDefault())
    
    /**
     * 格式化消息时间显示
     * 今天显示时间，昨天显示"昨天"，更早显示日期
     */
    fun formatMessageTime(date: Date): String {
        val now = Calendar.getInstance()
        val messageTime = Calendar.getInstance().apply { time = date }
        
        return when {
            isSameDay(now, messageTime) -> timeFormat.format(date)
            isYesterday(now, messageTime) -> "昨天"
            isSameYear(now, messageTime) -> dateFormat.format(date)
            else -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        }
    }
    
    /**
     * 格式化对话列表时间显示
     */
    fun formatConversationTime(date: Date): String {
        val now = Calendar.getInstance()
        val messageTime = Calendar.getInstance().apply { time = date }
        
        return when {
            isSameDay(now, messageTime) -> timeFormat.format(date)
            isYesterday(now, messageTime) -> "昨天"
            isSameWeek(now, messageTime) -> getWeekDay(messageTime)
            isSameYear(now, messageTime) -> dateFormat.format(date)
            else -> SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date)
        }
    }
    
    /**
     * 获取完整时间格式
     */
    fun formatFullTime(date: Date): String = fullFormat.format(date)
    
    /**
     * 判断是否为同一天
     */
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    /**
     * 判断是否为昨天
     */
    private fun isYesterday(now: Calendar, target: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply {
            time = now.time
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDay(yesterday, target)
    }
    
    /**
     * 判断是否为同一年
     */
    private fun isSameYear(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    }
    
    /**
     * 判断是否为同一周
     */
    private fun isSameWeek(now: Calendar, target: Calendar): Boolean {
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.WEEK_OF_YEAR) == target.get(Calendar.WEEK_OF_YEAR)
    }
    
    /**
     * 获取星期几
     */
    private fun getWeekDay(calendar: Calendar): String {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "周日"
            Calendar.MONDAY -> "周一"
            Calendar.TUESDAY -> "周二"
            Calendar.WEDNESDAY -> "周三"
            Calendar.THURSDAY -> "周四"
            Calendar.FRIDAY -> "周五"
            Calendar.SATURDAY -> "周六"
            else -> ""
        }
    }
}