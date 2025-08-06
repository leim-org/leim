package cn.lemwood.leim.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * 权限工具类
 * 用于管理应用权限申请和检查
 */
object PermissionUtils {
    
    // 权限请求码
    const val REQUEST_CODE_STORAGE = 1001
    const val REQUEST_CODE_NOTIFICATION = 1002
    const val REQUEST_CODE_CAMERA = 1003
    const val REQUEST_CODE_ALL_PERMISSIONS = 1004
    
    // 需要的权限列表
    val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }
    
    /**
     * 检查是否拥有指定权限
     */
    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 检查是否拥有所有必需权限
     */
    fun hasAllRequiredPermissions(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all { hasPermission(context, it) }
    }
    
    /**
     * 检查存储权限
     */
    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission(context, Manifest.permission.READ_MEDIA_IMAGES) &&
            hasPermission(context, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) &&
            hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
    
    /**
     * 检查通知权限
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true // Android 13以下默认有通知权限
        }
    }
    
    /**
     * 检查相机权限
     */
    fun hasCameraPermission(context: Context): Boolean {
        return hasPermission(context, Manifest.permission.CAMERA)
    }
    
    /**
     * 请求权限
     */
    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }
    
    /**
     * 请求多个权限
     */
    fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }
    
    /**
     * 请求所有必需权限
     */
    fun requestAllRequiredPermissions(activity: Activity) {
        val missingPermissions = REQUIRED_PERMISSIONS.filter { !hasPermission(activity, it) }
        if (missingPermissions.isNotEmpty()) {
            requestPermissions(activity, missingPermissions.toTypedArray(), REQUEST_CODE_ALL_PERMISSIONS)
        }
    }
    
    /**
     * 检查权限请求结果
     */
    fun isPermissionGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    }
    
    /**
     * 是否应该显示权限说明
     */
    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
}