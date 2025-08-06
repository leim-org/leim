package cn.lemwood.leim.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import java.io.*
import java.text.DecimalFormat
import java.util.*

/**
 * 文件操作工具类
 * 处理文件的创建、读取、写入、删除等操作
 */
object FileUtils {
    
    /**
     * 获取应用私有存储目录
     */
    fun getAppPrivateDir(context: Context): File {
        return context.filesDir
    }
    
    /**
     * 获取应用缓存目录
     */
    fun getAppCacheDir(context: Context): File {
        return context.cacheDir
    }
    
    /**
     * 获取外部存储目录
     */
    fun getExternalStorageDir(context: Context): File? {
        return context.getExternalFilesDir(null)
    }
    
    /**
     * 获取聊天文件存储目录
     */
    fun getChatFilesDir(context: Context): File {
        val dir = File(getAppPrivateDir(context), Constants.File.CHAT_FILES_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * 获取头像存储目录
     */
    fun getAvatarsDir(context: Context): File {
        val dir = File(getAppPrivateDir(context), Constants.File.AVATARS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * 获取图片存储目录
     */
    fun getImagesDir(context: Context): File {
        val dir = File(getChatFilesDir(context), Constants.File.IMAGES_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * 获取音频存储目录
     */
    fun getAudiosDir(context: Context): File {
        val dir = File(getChatFilesDir(context), Constants.File.AUDIOS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * 获取视频存储目录
     */
    fun getVideosDir(context: Context): File {
        val dir = File(getChatFilesDir(context), Constants.File.VIDEOS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * 获取文档存储目录
     */
    fun getDocumentsDir(context: Context): File {
        val dir = File(getChatFilesDir(context), Constants.File.DOCUMENTS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * 生成唯一文件名
     */
    fun generateUniqueFileName(originalName: String): String {
        val timestamp = System.currentTimeMillis()
        val random = UUID.randomUUID().toString().substring(0, 8)
        val extension = getFileExtension(originalName)
        val nameWithoutExt = getFileNameWithoutExtension(originalName)
        return "${nameWithoutExt}_${timestamp}_${random}${if (extension.isNotEmpty()) ".$extension" else ""}"
    }
    
    /**
     * 获取文件扩展名
     */
    fun getFileExtension(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex > 0 && lastDotIndex < fileName.length - 1) {
            fileName.substring(lastDotIndex + 1).lowercase()
        } else {
            ""
        }
    }
    
    /**
     * 获取不带扩展名的文件名
     */
    fun getFileNameWithoutExtension(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex > 0) {
            fileName.substring(0, lastDotIndex)
        } else {
            fileName
        }
    }
    
    /**
     * 根据文件扩展名获取MIME类型
     */
    fun getMimeType(fileName: String): String {
        val extension = getFileExtension(fileName)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/octet-stream"
    }
    
    /**
     * 判断是否为图片文件
     */
    fun isImageFile(fileName: String): Boolean {
        val extension = getFileExtension(fileName)
        return extension in Constants.File.IMAGE_EXTENSIONS
    }
    
    /**
     * 判断是否为音频文件
     */
    fun isAudioFile(fileName: String): Boolean {
        val extension = getFileExtension(fileName)
        return extension in Constants.File.AUDIO_EXTENSIONS
    }
    
    /**
     * 判断是否为视频文件
     */
    fun isVideoFile(fileName: String): Boolean {
        val extension = getFileExtension(fileName)
        return extension in Constants.File.VIDEO_EXTENSIONS
    }
    
    /**
     * 判断是否为文档文件
     */
    fun isDocumentFile(fileName: String): Boolean {
        val extension = getFileExtension(fileName)
        return extension in Constants.File.DOCUMENT_EXTENSIONS
    }
    
    /**
     * 获取文件类型
     */
    fun getFileType(fileName: String): String {
        return when {
            isImageFile(fileName) -> Constants.MessageType.IMAGE
            isAudioFile(fileName) -> Constants.MessageType.AUDIO
            isVideoFile(fileName) -> Constants.MessageType.VIDEO
            else -> Constants.MessageType.FILE
        }
    }
    
    /**
     * 格式化文件大小
     */
    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        
        return DecimalFormat("#,##0.#").format(bytes / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }
    
    /**
     * 复制文件
     */
    fun copyFile(source: File, destination: File): Boolean {
        return try {
            if (!destination.parentFile?.exists()!!) {
                destination.parentFile?.mkdirs()
            }
            
            source.inputStream().use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 从URI复制文件到指定位置
     */
    fun copyFileFromUri(context: Context, uri: Uri, destination: File): Boolean {
        return try {
            if (!destination.parentFile?.exists()!!) {
                destination.parentFile?.mkdirs()
            }
            
            context.contentResolver.openInputStream(uri)?.use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 删除文件
     */
    fun deleteFile(file: File): Boolean {
        return try {
            if (file.exists()) {
                if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    file.delete()
                }
            } else {
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 删除目录及其所有内容
     */
    fun deleteDirectory(directory: File): Boolean {
        return try {
            if (directory.exists() && directory.isDirectory) {
                directory.listFiles()?.forEach { file ->
                    if (file.isDirectory) {
                        deleteDirectory(file)
                    } else {
                        file.delete()
                    }
                }
                directory.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 获取文件大小
     */
    fun getFileSize(file: File): Long {
        return if (file.exists() && file.isFile) {
            file.length()
        } else {
            0L
        }
    }
    
    /**
     * 获取目录大小
     */
    fun getDirectorySize(directory: File): Long {
        var size = 0L
        if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                size += if (file.isDirectory) {
                    getDirectorySize(file)
                } else {
                    file.length()
                }
            }
        }
        return size
    }
    
    /**
     * 清理缓存目录
     */
    fun clearCache(context: Context): Boolean {
        return try {
            val cacheDir = getAppCacheDir(context)
            deleteDirectory(cacheDir)
            cacheDir.mkdirs()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 获取缓存大小
     */
    fun getCacheSize(context: Context): Long {
        return getDirectorySize(getAppCacheDir(context))
    }
    
    /**
     * 压缩图片
     */
    fun compressImage(context: Context, sourceFile: File, quality: Int = 80): File? {
        return try {
            val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath)
            val compressedFile = File(getAppCacheDir(context), "compressed_${sourceFile.name}")
            
            compressedFile.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
            }
            
            bitmap.recycle()
            compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 调整图片大小
     */
    fun resizeImage(context: Context, sourceFile: File, maxWidth: Int, maxHeight: Int): File? {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(sourceFile.absolutePath, options)
            
            val originalWidth = options.outWidth
            val originalHeight = options.outHeight
            
            var inSampleSize = 1
            if (originalHeight > maxHeight || originalWidth > maxWidth) {
                val halfHeight = originalHeight / 2
                val halfWidth = originalWidth / 2
                
                while ((halfHeight / inSampleSize) >= maxHeight && (halfWidth / inSampleSize) >= maxWidth) {
                    inSampleSize *= 2
                }
            }
            
            options.inSampleSize = inSampleSize
            options.inJustDecodeBounds = false
            
            val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath, options)
            val resizedFile = File(getAppCacheDir(context), "resized_${sourceFile.name}")
            
            resizedFile.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
            }
            
            bitmap.recycle()
            resizedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 创建头像文件
     */
    fun createAvatarFile(context: Context, userId: String): File {
        val avatarsDir = getAvatarsDir(context)
        return File(avatarsDir, "avatar_${userId}.jpg")
    }
    
    /**
     * 保存头像
     */
    fun saveAvatar(context: Context, userId: String, bitmap: Bitmap): File? {
        return try {
            val avatarFile = createAvatarFile(context, userId)
            avatarFile.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
            }
            avatarFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 获取头像文件
     */
    fun getAvatarFile(context: Context, userId: String): File? {
        val avatarFile = createAvatarFile(context, userId)
        return if (avatarFile.exists()) avatarFile else null
    }
    
    /**
     * 写入文本文件
     */
    fun writeTextFile(file: File, content: String): Boolean {
        return try {
            if (!file.parentFile?.exists()!!) {
                file.parentFile?.mkdirs()
            }
            file.writeText(content, Charsets.UTF_8)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 读取文本文件
     */
    fun readTextFile(file: File): String? {
        return try {
            if (file.exists() && file.isFile) {
                file.readText(Charsets.UTF_8)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 检查存储空间是否足够
     */
    fun hasEnoughSpace(context: Context, requiredBytes: Long): Boolean {
        return try {
            val availableBytes = getAppPrivateDir(context).freeSpace
            availableBytes >= requiredBytes
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取可用存储空间
     */
    fun getAvailableSpace(context: Context): Long {
        return try {
            getAppPrivateDir(context).freeSpace
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * 创建临时文件
     */
    fun createTempFile(context: Context, prefix: String, suffix: String): File? {
        return try {
            File.createTempFile(prefix, suffix, getAppCacheDir(context))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 清理过期的临时文件
     */
    fun cleanExpiredTempFiles(context: Context, maxAgeMillis: Long = 24 * 60 * 60 * 1000) {
        try {
            val cacheDir = getAppCacheDir(context)
            val currentTime = System.currentTimeMillis()
            
            cacheDir.listFiles()?.forEach { file ->
                if (currentTime - file.lastModified() > maxAgeMillis) {
                    deleteFile(file)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 获取文件的MD5值
     */
    fun getFileMD5(file: File): String? {
        return try {
            val digest = java.security.MessageDigest.getInstance("MD5")
            file.inputStream().use { input ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 检查文件是否存在且可读
     */
    fun isFileReadable(file: File): Boolean {
        return file.exists() && file.isFile && file.canRead()
    }
    
    /**
     * 检查目录是否存在且可写
     */
    fun isDirectoryWritable(directory: File): Boolean {
        return directory.exists() && directory.isDirectory && directory.canWrite()
    }
}