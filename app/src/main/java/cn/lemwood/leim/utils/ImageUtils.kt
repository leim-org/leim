package cn.lemwood.leim.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import androidx.core.content.ContextCompat
import java.io.*
import kotlin.math.*

/**
 * 图片处理工具类
 * 提供图片压缩、裁剪、旋转、格式转换等功能
 */
object ImageUtils {
    
    /**
     * 从Uri加载Bitmap
     */
    fun loadBitmapFromUri(context: Context, uri: Uri, maxWidth: Int = 1920, maxHeight: Int = 1080): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()
            
            // 计算缩放比例
            val scaleFactor = calculateInSampleSize(options, maxWidth, maxHeight)
            
            val finalInputStream = context.contentResolver.openInputStream(uri)
            val finalOptions = BitmapFactory.Options().apply {
                inSampleSize = scaleFactor
                inJustDecodeBounds = false
            }
            val bitmap = BitmapFactory.decodeStream(finalInputStream, null, finalOptions)
            finalInputStream?.close()
            
            // 处理图片旋转
            bitmap?.let { correctImageOrientation(context, uri, it) }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 从文件路径加载Bitmap
     */
    fun loadBitmapFromFile(filePath: String, maxWidth: Int = 1920, maxHeight: Int = 1080): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(filePath, options)
            
            val scaleFactor = calculateInSampleSize(options, maxWidth, maxHeight)
            
            val finalOptions = BitmapFactory.Options().apply {
                inSampleSize = scaleFactor
                inJustDecodeBounds = false
            }
            val bitmap = BitmapFactory.decodeFile(filePath, finalOptions)
            
            // 处理图片旋转
            bitmap?.let { correctImageOrientation(filePath, it) }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 计算图片缩放比例
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
    
    /**
     * 修正图片方向
     */
    private fun correctImageOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = ExifInterface(inputStream!!)
            inputStream.close()
            
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            rotateBitmap(bitmap, orientation)
        } catch (e: Exception) {
            bitmap
        }
    }
    
    /**
     * 修正图片方向（文件路径）
     */
    private fun correctImageOrientation(filePath: String, bitmap: Bitmap): Bitmap {
        return try {
            val exif = ExifInterface(filePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            rotateBitmap(bitmap, orientation)
        } catch (e: Exception) {
            bitmap
        }
    }
    
    /**
     * 根据EXIF信息旋转图片
     */
    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
            else -> return bitmap
        }
        
        return try {
            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            rotatedBitmap
        } catch (e: Exception) {
            bitmap
        }
    }
    
    /**
     * 压缩图片到指定大小
     */
    fun compressBitmap(bitmap: Bitmap, maxSizeKB: Int = 500): ByteArray? {
        return try {
            var quality = 90
            val outputStream = ByteArrayOutputStream()
            
            do {
                outputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                quality -= 10
            } while (outputStream.size() > maxSizeKB * 1024 && quality > 10)
            
            outputStream.toByteArray()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 缩放图片
     */
    fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }
        
        val scaleWidth = maxWidth.toFloat() / width
        val scaleHeight = maxHeight.toFloat() / height
        val scale = min(scaleWidth, scaleHeight)
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * 裁剪图片为圆形
     */
    fun cropToCircle(bitmap: Bitmap): Bitmap {
        val size = min(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.RED
        }
        
        val rect = Rect(0, 0, size, size)
        val rectF = RectF(rect)
        
        canvas.drawOval(rectF, paint)
        
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        
        val sourceRect = if (bitmap.width > bitmap.height) {
            Rect((bitmap.width - bitmap.height) / 2, 0, (bitmap.width + bitmap.height) / 2, bitmap.height)
        } else {
            Rect(0, (bitmap.height - bitmap.width) / 2, bitmap.width, (bitmap.height + bitmap.width) / 2)
        }
        
        canvas.drawBitmap(bitmap, sourceRect, rect, paint)
        
        return output
    }
    
    /**
     * 裁剪图片为圆角矩形
     */
    fun cropToRoundedRectangle(bitmap: Bitmap, cornerRadius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.RED
        }
        
        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        
        return output
    }
    
    /**
     * 添加边框
     */
    fun addBorder(bitmap: Bitmap, borderWidth: Int, borderColor: Int): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width + borderWidth * 2,
            bitmap.height + borderWidth * 2,
            Bitmap.Config.ARGB_8888
        )
        
        val canvas = Canvas(output)
        val paint = Paint().apply {
            color = borderColor
            style = Paint.Style.FILL
        }
        
        canvas.drawRect(0f, 0f, output.width.toFloat(), output.height.toFloat(), paint)
        canvas.drawBitmap(bitmap, borderWidth.toFloat(), borderWidth.toFloat(), null)
        
        return output
    }
    
    /**
     * 生成文字图片
     */
    fun createTextBitmap(text: String, textSize: Float, textColor: Int, backgroundColor: Int, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // 绘制背景
        canvas.drawColor(backgroundColor)
        
        // 绘制文字
        val paint = Paint().apply {
            color = textColor
            this.textSize = textSize
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        
        val fontMetrics = paint.fontMetrics
        val textHeight = fontMetrics.bottom - fontMetrics.top
        val textY = (height - textHeight) / 2 - fontMetrics.top
        
        canvas.drawText(text, width / 2f, textY, paint)
        
        return bitmap
    }
    
    /**
     * Bitmap转Base64
     */
    fun bitmapToBase64(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 90): String? {
        return try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(format, quality, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Base64转Bitmap
     */
    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64String, Base64.NO_WRAP)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Drawable转Bitmap
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        
        return bitmap
    }
    
    /**
     * 保存Bitmap到文件
     */
    fun saveBitmapToFile(bitmap: Bitmap, file: File, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 90): Boolean {
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(format, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取图片尺寸信息
     */
    fun getImageSize(filePath: String): Pair<Int, Int>? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(filePath, options)
            Pair(options.outWidth, options.outHeight)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 检查是否为图片文件
     */
    fun isImageFile(filePath: String): Boolean {
        val extension = filePath.substringAfterLast('.', "").lowercase()
        return extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }
    
    /**
     * 生成默认头像
     */
    fun generateDefaultAvatar(context: Context, text: String, size: Int = 200): Bitmap {
        val colors = arrayOf(
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
            "#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9"
        )
        
        val colorIndex = abs(text.hashCode()) % colors.size
        val backgroundColor = Color.parseColor(colors[colorIndex])
        
        val displayText = if (text.length >= 2) {
            text.substring(0, 2).uppercase()
        } else {
            text.uppercase()
        }
        
        return createTextBitmap(
            text = displayText,
            textSize = size * 0.4f,
            textColor = Color.WHITE,
            backgroundColor = backgroundColor,
            width = size,
            height = size
        )
    }
}