package cn.lemwood.leim.utils

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * 异常行为日志捕获类
 * 用于捕获应用中的异常并保存到本地存储
 */
class CrashLogger private constructor(private val context: Context) : Thread.UncaughtExceptionHandler {
    
    companion object {
        private const val TAG = "CrashLogger"
        private const val LOG_DIR = "crash_logs"
        private const val LOG_FILE_PREFIX = "crash_"
        private const val LOG_FILE_SUFFIX = ".log"
        private const val MAX_LOG_FILES = 10 // 最多保留10个日志文件
        
        @Volatile
        private var INSTANCE: CrashLogger? = null
        
        /**
         * 初始化异常日志捕获器
         */
        fun init(context: Context): CrashLogger {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CrashLogger(context.applicationContext).also { 
                    INSTANCE = it
                    // 设置为默认的未捕获异常处理器
                    Thread.setDefaultUncaughtExceptionHandler(it)
                }
            }
        }
        
        /**
         * 获取实例
         */
        fun getInstance(): CrashLogger? = INSTANCE
    }
    
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
    private val logDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    /**
     * 处理未捕获的异常
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            // 记录异常到本地文件
            saveCrashLog(e, t)
            Log.e(TAG, "应用发生未捕获异常", e)
        } catch (ex: Exception) {
            Log.e(TAG, "保存异常日志失败", ex)
        }
        
        // 调用系统默认的异常处理器
        defaultHandler?.uncaughtException(t, e)
    }
    
    /**
     * 手动记录异常
     */
    fun logException(throwable: Throwable, tag: String = "ManualLog") {
        try {
            saveCrashLog(throwable, Thread.currentThread(), tag)
            Log.e(TAG, "手动记录异常: $tag", throwable)
        } catch (e: Exception) {
            Log.e(TAG, "手动保存异常日志失败", e)
        }
    }
    
    /**
     * 记录错误信息
     */
    fun logError(message: String, throwable: Throwable? = null) {
        try {
            val logContent = buildString {
                appendLine("=== 错误日志 ===")
                appendLine("时间: ${logDateFormat.format(Date())}")
                appendLine("消息: $message")
                if (throwable != null) {
                    appendLine("异常信息:")
                    appendLine(getStackTraceString(throwable))
                }
                appendLine("设备信息:")
                appendLine(getDeviceInfo())
                appendLine("=== 日志结束 ===")
                appendLine()
            }
            
            saveLogToFile(logContent, "error")
            Log.e(TAG, "记录错误: $message", throwable)
        } catch (e: Exception) {
            Log.e(TAG, "保存错误日志失败", e)
        }
    }
    
    /**
     * 保存异常日志到文件
     */
    private fun saveCrashLog(throwable: Throwable, thread: Thread, tag: String = "Crash") {
        val logContent = buildString {
            appendLine("=== $tag 异常日志 ===")
            appendLine("时间: ${logDateFormat.format(Date())}")
            appendLine("线程: ${thread.name}")
            appendLine("异常类型: ${throwable.javaClass.name}")
            appendLine("异常消息: ${throwable.message}")
            appendLine()
            appendLine("异常堆栈:")
            appendLine(getStackTraceString(throwable))
            appendLine()
            appendLine("设备信息:")
            appendLine(getDeviceInfo())
            appendLine("=== 日志结束 ===")
            appendLine()
        }
        
        saveLogToFile(logContent, "crash")
    }
    
    /**
     * 保存日志内容到文件
     */
    private fun saveLogToFile(content: String, type: String) {
        try {
            val logDir = File(context.filesDir, LOG_DIR)
            if (!logDir.exists()) {
                logDir.mkdirs()
            }
            
            val fileName = "${LOG_FILE_PREFIX}${type}_${dateFormat.format(Date())}$LOG_FILE_SUFFIX"
            val logFile = File(logDir, fileName)
            
            FileWriter(logFile, true).use { writer ->
                writer.write(content)
                writer.flush()
            }
            
            // 清理旧的日志文件
            cleanOldLogFiles(logDir)
            
        } catch (e: Exception) {
            Log.e(TAG, "写入日志文件失败", e)
        }
    }
    
    /**
     * 获取异常堆栈信息
     */
    private fun getStackTraceString(throwable: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)
        printWriter.close()
        return stringWriter.toString()
    }
    
    /**
     * 获取设备信息
     */
    private fun getDeviceInfo(): String {
        return buildString {
            appendLine("设备型号: ${Build.MODEL}")
            appendLine("设备品牌: ${Build.BRAND}")
            appendLine("系统版本: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("应用版本: ${getAppVersion()}")
            appendLine("可用内存: ${getAvailableMemory()}")
        }
    }
    
    /**
     * 获取应用版本信息
     */
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: Exception) {
            "未知版本"
        }
    }
    
    /**
     * 获取可用内存信息
     */
    private fun getAvailableMemory(): String {
        return try {
            val runtime = Runtime.getRuntime()
            val maxMemory = runtime.maxMemory() / 1024 / 1024
            val totalMemory = runtime.totalMemory() / 1024 / 1024
            val freeMemory = runtime.freeMemory() / 1024 / 1024
            val usedMemory = totalMemory - freeMemory
            
            "最大内存: ${maxMemory}MB, 已用内存: ${usedMemory}MB, 可用内存: ${freeMemory}MB"
        } catch (e: Exception) {
            "内存信息获取失败"
        }
    }
    
    /**
     * 清理旧的日志文件，只保留最新的几个
     */
    private fun cleanOldLogFiles(logDir: File) {
        try {
            val logFiles = logDir.listFiles { file ->
                file.name.startsWith(LOG_FILE_PREFIX) && file.name.endsWith(LOG_FILE_SUFFIX)
            }
            
            if (logFiles != null && logFiles.size > MAX_LOG_FILES) {
                // 按修改时间排序，删除最旧的文件
                logFiles.sortBy { it.lastModified() }
                val filesToDelete = logFiles.take(logFiles.size - MAX_LOG_FILES)
                filesToDelete.forEach { file ->
                    if (file.delete()) {
                        Log.d(TAG, "删除旧日志文件: ${file.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "清理旧日志文件失败", e)
        }
    }
    
    /**
     * 获取所有日志文件
     */
    fun getLogFiles(): List<File> {
        return try {
            val logDir = File(context.filesDir, LOG_DIR)
            if (logDir.exists()) {
                logDir.listFiles { file ->
                    file.name.startsWith(LOG_FILE_PREFIX) && file.name.endsWith(LOG_FILE_SUFFIX)
                }?.toList() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取日志文件列表失败", e)
            emptyList()
        }
    }
    
    /**
     * 删除所有日志文件
     */
    fun clearAllLogs(): Boolean {
        return try {
            val logDir = File(context.filesDir, LOG_DIR)
            if (logDir.exists()) {
                logDir.deleteRecursively()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "清除所有日志失败", e)
            false
        }
    }
    
    /**
     * 获取日志目录大小
     */
    fun getLogDirectorySize(): Long {
        return try {
            val logDir = File(context.filesDir, LOG_DIR)
            if (logDir.exists()) {
                logDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
            } else {
                0L
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取日志目录大小失败", e)
            0L
        }
    }
}