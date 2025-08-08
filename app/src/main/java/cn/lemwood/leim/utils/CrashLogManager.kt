package cn.lemwood.leim.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 崩溃日志管理工具类
 * 提供查看、分享、清理崩溃日志的功能
 */
object CrashLogManager {
    
    private const val TAG = "CrashLogManager"
    
    /**
     * 崩溃日志信息数据类
     */
    data class CrashLogInfo(
        val file: File,
        val fileName: String,
        val fileSize: Long,
        val lastModified: Date,
        val formattedSize: String,
        val formattedDate: String
    )
    
    /**
     * 获取所有崩溃日志信息
     */
    fun getCrashLogInfoList(context: Context): List<CrashLogInfo> {
        val logFiles = CrashLogger.getCrashLogFiles(context)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        return logFiles.map { file ->
            CrashLogInfo(
                file = file,
                fileName = file.name,
                fileSize = file.length(),
                lastModified = Date(file.lastModified()),
                formattedSize = formatFileSize(file.length()),
                formattedDate = dateFormat.format(Date(file.lastModified()))
            )
        }.sortedByDescending { it.lastModified } // 按时间倒序排列
    }
    
    /**
     * 读取崩溃日志内容
     */
    fun readCrashLog(file: File): String {
        return try {
            file.readText()
        } catch (e: Exception) {
            "读取日志文件失败: ${e.message}"
        }
    }
    
    /**
     * 分享崩溃日志文件
     */
    fun shareCrashLog(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Leim 崩溃日志 - ${file.name}")
                putExtra(Intent.EXTRA_TEXT, "Leim 应用崩溃日志文件")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "分享崩溃日志"))
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to share crash log", e)
        }
    }
    
    /**
     * 删除指定的崩溃日志文件
     */
    fun deleteCrashLog(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to delete crash log", e)
            false
        }
    }
    
    /**
     * 清理所有崩溃日志
     */
    fun clearAllCrashLogs(context: Context) {
        CrashLogger.clearCrashLogs(context)
    }
    
    /**
     * 获取崩溃日志总数
     */
    fun getCrashLogCount(context: Context): Int {
        return CrashLogger.getCrashLogFiles(context).size
    }
    
    /**
     * 获取崩溃日志总大小
     */
    fun getTotalCrashLogSize(context: Context): Long {
        return CrashLogger.getCrashLogFiles(context).sumOf { it.length() }
    }
    
    /**
     * 格式化文件大小
     */
    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }
    
    /**
     * 检查是否有崩溃日志
     */
    fun hasCrashLogs(context: Context): Boolean {
        return getCrashLogCount(context) > 0
    }
    
    /**
     * 获取最新的崩溃日志
     */
    fun getLatestCrashLog(context: Context): File? {
        val logFiles = CrashLogger.getCrashLogFiles(context)
        return logFiles.maxByOrNull { it.lastModified() }
    }
    
    /**
     * 手动触发异常记录（用于测试）
     */
    fun testCrashLog() {
        try {
            throw RuntimeException("这是一个测试异常，用于验证崩溃日志功能")
        } catch (e: Exception) {
            CrashLogger.logException(e, "TestException")
        }
    }
}