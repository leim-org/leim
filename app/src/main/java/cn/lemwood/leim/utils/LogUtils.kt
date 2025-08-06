package cn.lemwood.leim.utils

import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors

/**
 * 日志工具类
 * 提供统一的日志管理功能，支持控制台输出和文件保存
 */
object LogUtils {
    
    private const val TAG = "LeimApp"
    private const val MAX_LOG_FILES = 7 // 保留最近7天的日志
    private const val MAX_LOG_FILE_SIZE = 10 * 1024 * 1024 // 10MB
    
    private var isDebug = true
    private var enableFileLog = true
    private var logDirectory: File? = null
    
    private val logQueue = ConcurrentLinkedQueue<LogEntry>()
    private val executor = Executors.newSingleThreadExecutor()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val fileDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    /**
     * 初始化日志工具
     */
    fun init(isDebug: Boolean = true, enableFileLog: Boolean = true, logDir: File? = null) {
        this.isDebug = isDebug
        this.enableFileLog = enableFileLog
        this.logDirectory = logDir
        
        if (enableFileLog && logDir != null) {
            // 清理旧日志文件
            cleanOldLogFiles()
            
            // 启动日志写入线程
            startLogWriterThread()
        }
    }
    
    /**
     * Verbose级别日志
     */
    fun v(message: String, tag: String = TAG) {
        log(LogLevel.VERBOSE, tag, message, null)
    }
    
    fun v(message: String, throwable: Throwable?, tag: String = TAG) {
        log(LogLevel.VERBOSE, tag, message, throwable)
    }
    
    /**
     * Debug级别日志
     */
    fun d(message: String, tag: String = TAG) {
        log(LogLevel.DEBUG, tag, message, null)
    }
    
    fun d(message: String, throwable: Throwable?, tag: String = TAG) {
        log(LogLevel.DEBUG, tag, message, throwable)
    }
    
    /**
     * Info级别日志
     */
    fun i(message: String, tag: String = TAG) {
        log(LogLevel.INFO, tag, message, null)
    }
    
    fun i(message: String, throwable: Throwable?, tag: String = TAG) {
        log(LogLevel.INFO, tag, message, throwable)
    }
    
    /**
     * Warning级别日志
     */
    fun w(message: String, tag: String = TAG) {
        log(LogLevel.WARNING, tag, message, null)
    }
    
    fun w(message: String, throwable: Throwable?, tag: String = TAG) {
        log(LogLevel.WARNING, tag, message, throwable)
    }
    
    /**
     * Error级别日志
     */
    fun e(message: String, tag: String = TAG) {
        log(LogLevel.ERROR, tag, message, null)
    }
    
    fun e(message: String, throwable: Throwable?, tag: String = TAG) {
        log(LogLevel.ERROR, tag, message, throwable)
    }
    
    /**
     * WebSocket相关日志
     */
    fun ws(message: String, tag: String = "WebSocket") {
        log(LogLevel.DEBUG, tag, message, null)
    }
    
    /**
     * 网络请求日志
     */
    fun network(message: String, tag: String = "Network") {
        log(LogLevel.DEBUG, tag, message, null)
    }
    
    /**
     * 数据库操作日志
     */
    fun db(message: String, tag: String = "Database") {
        log(LogLevel.DEBUG, tag, message, null)
    }
    
    /**
     * UI操作日志
     */
    fun ui(message: String, tag: String = "UI") {
        log(LogLevel.DEBUG, tag, message, null)
    }
    
    /**
     * 文件操作日志
     */
    fun file(message: String, tag: String = "File") {
        log(LogLevel.DEBUG, tag, message, null)
    }
    
    /**
     * 权限相关日志
     */
    fun permission(message: String, tag: String = "Permission") {
        log(LogLevel.INFO, tag, message, null)
    }
    
    /**
     * 性能监控日志
     */
    fun performance(message: String, tag: String = "Performance") {
        log(LogLevel.INFO, tag, message, null)
    }
    
    /**
     * 统一日志处理方法
     */
    private fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        // 控制台输出
        if (isDebug) {
            val fullMessage = if (throwable != null) {
                "$message\n${Log.getStackTraceString(throwable)}"
            } else {
                message
            }
            
            when (level) {
                LogLevel.VERBOSE -> Log.v(tag, fullMessage)
                LogLevel.DEBUG -> Log.d(tag, fullMessage)
                LogLevel.INFO -> Log.i(tag, fullMessage)
                LogLevel.WARNING -> Log.w(tag, fullMessage)
                LogLevel.ERROR -> Log.e(tag, fullMessage)
            }
        }
        
        // 文件输出
        if (enableFileLog && logDirectory != null) {
            val logEntry = LogEntry(
                timestamp = System.currentTimeMillis(),
                level = level,
                tag = tag,
                message = message,
                throwable = throwable
            )
            logQueue.offer(logEntry)
        }
    }
    
    /**
     * 启动日志写入线程
     */
    private fun startLogWriterThread() {
        executor.execute {
            while (true) {
                try {
                    val logEntry = logQueue.poll()
                    if (logEntry != null) {
                        writeLogToFile(logEntry)
                    } else {
                        Thread.sleep(100) // 没有日志时休眠100ms
                    }
                } catch (e: Exception) {
                    // 忽略写入异常，避免无限循环
                }
            }
        }
    }
    
    /**
     * 写入日志到文件
     */
    private fun writeLogToFile(logEntry: LogEntry) {
        try {
            val logFile = getCurrentLogFile()
            if (logFile != null && logFile.length() < MAX_LOG_FILE_SIZE) {
                FileWriter(logFile, true).use { writer ->
                    val timestamp = dateFormat.format(Date(logEntry.timestamp))
                    val levelStr = logEntry.level.name.substring(0, 1)
                    
                    writer.append("$timestamp $levelStr/${logEntry.tag}: ${logEntry.message}\n")
                    
                    if (logEntry.throwable != null) {
                        writer.append("${Log.getStackTraceString(logEntry.throwable)}\n")
                    }
                    
                    writer.flush()
                }
            }
        } catch (e: IOException) {
            // 忽略文件写入异常
        }
    }
    
    /**
     * 获取当前日志文件
     */
    private fun getCurrentLogFile(): File? {
        val logDir = logDirectory ?: return null
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        
        val today = fileDateFormat.format(Date())
        return File(logDir, "leim_$today.log")
    }
    
    /**
     * 清理旧日志文件
     */
    private fun cleanOldLogFiles() {
        val logDir = logDirectory ?: return
        if (!logDir.exists()) return
        
        val cutoffTime = System.currentTimeMillis() - (MAX_LOG_FILES * 24 * 60 * 60 * 1000L)
        
        logDir.listFiles { file ->
            file.name.startsWith("leim_") && file.name.endsWith(".log")
        }?.forEach { file ->
            if (file.lastModified() < cutoffTime) {
                file.delete()
            }
        }
    }
    
    /**
     * 获取调用栈信息
     */
    fun getStackTrace(): String {
        val stackTrace = Thread.currentThread().stackTrace
        val sb = StringBuilder()
        
        // 跳过前几个系统调用栈
        for (i in 4 until minOf(stackTrace.size, 10)) {
            val element = stackTrace[i]
            sb.append("    at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})\n")
        }
        
        return sb.toString()
    }
    
    /**
     * 记录方法执行时间
     */
    inline fun <T> measureTime(tag: String = "Performance", operation: String, block: () -> T): T {
        val startTime = System.currentTimeMillis()
        val result = block()
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        performance("$operation took ${duration}ms", tag)
        return result
    }
    
    /**
     * 记录异常信息
     */
    fun logException(throwable: Throwable, tag: String = "Exception", message: String = "Unexpected error occurred") {
        e(message, throwable, tag)
    }
    
    /**
     * 记录用户操作
     */
    fun logUserAction(action: String, details: String = "") {
        val message = if (details.isNotEmpty()) {
            "User action: $action - $details"
        } else {
            "User action: $action"
        }
        i(message, "UserAction")
    }
    
    /**
     * 记录应用生命周期
     */
    fun logLifecycle(component: String, event: String) {
        i("$component - $event", "Lifecycle")
    }
    
    /**
     * 获取日志文件列表
     */
    fun getLogFiles(): List<File> {
        val logDir = logDirectory ?: return emptyList()
        if (!logDir.exists()) return emptyList()
        
        return logDir.listFiles { file ->
            file.name.startsWith("leim_") && file.name.endsWith(".log")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
    
    /**
     * 清空所有日志文件
     */
    fun clearAllLogs() {
        getLogFiles().forEach { it.delete() }
        logQueue.clear()
    }
    
    /**
     * 日志级别枚举
     */
    enum class LogLevel {
        VERBOSE, DEBUG, INFO, WARNING, ERROR
    }
    
    /**
     * 日志条目数据类
     */
    private data class LogEntry(
        val timestamp: Long,
        val level: LogLevel,
        val tag: String,
        val message: String,
        val throwable: Throwable?
    )
}