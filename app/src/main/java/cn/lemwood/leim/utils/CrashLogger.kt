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
import kotlin.system.exitProcess

/**
 * 异常行为日志捕获类
 * 用于捕获应用中的未处理异常并保存到本地存储
 */
class CrashLogger private constructor(private val context: Context) : Thread.UncaughtExceptionHandler {

    companion object {
        private const val TAG = "CrashLogger"
        private const val CRASH_LOG_DIR = "crash_logs"
        private const val MAX_LOG_FILES = 10 // 最多保留10个日志文件
        
        @Volatile
        private var INSTANCE: CrashLogger? = null
        
        /**
         * 初始化异常日志捕获器
         */
        fun init(context: Context) {
            if (INSTANCE == null) {
                synchronized(CrashLogger::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = CrashLogger(context.applicationContext)
                        Thread.setDefaultUncaughtExceptionHandler(INSTANCE)
                        Log.d(TAG, "CrashLogger initialized")
                    }
                }
            }
        }
        
        /**
         * 手动记录异常
         */
        fun logException(throwable: Throwable, tag: String = "ManualLog") {
            INSTANCE?.let { logger ->
                try {
                    logger.saveExceptionToFile(throwable, tag)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to log exception manually", e)
                }
            }
        }
        
        /**
         * 获取所有崩溃日志文件
         */
        fun getCrashLogFiles(context: Context): List<File> {
            val crashDir = File(context.filesDir, CRASH_LOG_DIR)
            return if (crashDir.exists() && crashDir.isDirectory) {
                crashDir.listFiles()?.toList() ?: emptyList()
            } else {
                emptyList()
            }
        }
        
        /**
         * 清理所有崩溃日志
         */
        fun clearCrashLogs(context: Context) {
            val crashDir = File(context.filesDir, CRASH_LOG_DIR)
            if (crashDir.exists()) {
                crashDir.listFiles()?.forEach { it.delete() }
                Log.d(TAG, "All crash logs cleared")
            }
        }
    }

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
    private val logDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            // 保存异常信息到文件
            saveExceptionToFile(throwable, "UncaughtException")
            
            // 记录到系统日志
            Log.e(TAG, "Uncaught exception in thread ${thread.name}", throwable)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save crash log", e)
        } finally {
            // 调用默认的异常处理器
            defaultHandler?.uncaughtException(thread, throwable)
            
            // 如果默认处理器为空，则退出应用
            if (defaultHandler == null) {
                android.os.Process.killProcess(android.os.Process.myPid())
                exitProcess(1)
            }
        }
    }

    /**
     * 保存异常信息到文件
     */
    private fun saveExceptionToFile(throwable: Throwable, tag: String) {
        try {
            // 创建崩溃日志目录
            val crashDir = File(context.filesDir, CRASH_LOG_DIR)
            if (!crashDir.exists()) {
                crashDir.mkdirs()
            }

            // 清理旧的日志文件
            cleanOldLogFiles(crashDir)

            // 创建日志文件
            val timestamp = dateFormat.format(Date())
            val logFile = File(crashDir, "crash_$timestamp.log")

            // 写入异常信息
            FileWriter(logFile, true).use { fileWriter ->
                PrintWriter(fileWriter).use { printWriter ->
                    // 写入基本信息
                    printWriter.println("=== CRASH LOG ===")
                    printWriter.println("Time: ${logDateFormat.format(Date())}")
                    printWriter.println("Tag: $tag")
                    printWriter.println("Thread: ${Thread.currentThread().name}")
                    printWriter.println()
                    
                    // 写入设备信息
                    printWriter.println("=== DEVICE INFO ===")
                    printWriter.println("Brand: ${Build.BRAND}")
                    printWriter.println("Model: ${Build.MODEL}")
                    printWriter.println("Device: ${Build.DEVICE}")
                    printWriter.println("Android Version: ${Build.VERSION.RELEASE}")
                    printWriter.println("API Level: ${Build.VERSION.SDK_INT}")
                    printWriter.println("Manufacturer: ${Build.MANUFACTURER}")
                    printWriter.println()
                    
                    // 写入应用信息
                    printWriter.println("=== APP INFO ===")
                    try {
                        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                        printWriter.println("Package: ${packageInfo.packageName}")
                        printWriter.println("Version Name: ${packageInfo.versionName}")
                        printWriter.println("Version Code: ${packageInfo.versionCode}")
                    } catch (e: Exception) {
                        printWriter.println("Failed to get app info: ${e.message}")
                    }
                    printWriter.println()
                    
                    // 写入异常堆栈
                    printWriter.println("=== EXCEPTION STACK TRACE ===")
                    val stringWriter = StringWriter()
                    throwable.printStackTrace(PrintWriter(stringWriter))
                    printWriter.println(stringWriter.toString())
                    
                    printWriter.println("=== END OF LOG ===")
                    printWriter.println()
                }
            }

            Log.d(TAG, "Crash log saved to: ${logFile.absolutePath}")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to save exception to file", e)
        }
    }

    /**
     * 清理旧的日志文件，只保留最新的几个
     */
    private fun cleanOldLogFiles(crashDir: File) {
        try {
            val logFiles = crashDir.listFiles { _, name -> 
                name.startsWith("crash_") && name.endsWith(".log") 
            }
            
            if (logFiles != null && logFiles.size >= MAX_LOG_FILES) {
                // 按修改时间排序，删除最旧的文件
                val sortedFiles = logFiles.sortedBy { it.lastModified() }
                val filesToDelete = sortedFiles.take(sortedFiles.size - MAX_LOG_FILES + 1)
                
                filesToDelete.forEach { file ->
                    if (file.delete()) {
                        Log.d(TAG, "Deleted old crash log: ${file.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clean old log files", e)
        }
    }
}