package cn.lemwood.leim.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import java.io.File
import java.net.NetworkInterface
import java.util.*

/**
 * 设备信息工具类
 * 提供设备硬件信息、系统信息、存储信息等
 */
object DeviceUtils {
    
    /**
     * 获取设备唯一标识符
     */
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * 获取设备型号
     */
    fun getDeviceModel(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }
    
    /**
     * 获取设备品牌
     */
    fun getDeviceBrand(): String {
        return Build.BRAND
    }
    
    /**
     * 获取设备制造商
     */
    fun getDeviceManufacturer(): String {
        return Build.MANUFACTURER
    }
    
    /**
     * 获取Android版本
     */
    fun getAndroidVersion(): String {
        return Build.VERSION.RELEASE
    }
    
    /**
     * 获取Android SDK版本
     */
    fun getAndroidSDK(): Int {
        return Build.VERSION.SDK_INT
    }
    
    /**
     * 获取应用版本名称
     */
    fun getAppVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * 获取应用版本号
     */
    fun getAppVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * 获取屏幕宽度（像素）
     */
    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
    
    /**
     * 获取屏幕高度（像素）
     */
    fun getScreenHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
    
    /**
     * 获取屏幕密度
     */
    fun getScreenDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }
    
    /**
     * 获取屏幕DPI
     */
    fun getScreenDPI(context: Context): Int {
        return context.resources.displayMetrics.densityDpi
    }
    
    /**
     * dp转px
     */
    fun dp2px(context: Context, dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
    
    /**
     * px转dp
     */
    fun px2dp(context: Context, px: Float): Int {
        val density = context.resources.displayMetrics.density
        return (px / density + 0.5f).toInt()
    }
    
    /**
     * sp转px
     */
    fun sp2px(context: Context, sp: Float): Int {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return (sp * scaledDensity + 0.5f).toInt()
    }
    
    /**
     * px转sp
     */
    fun px2sp(context: Context, px: Float): Int {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return (px / scaledDensity + 0.5f).toInt()
    }
    
    /**
     * 是否为平板设备
     */
    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
    
    /**
     * 获取总内存大小（MB）
     */
    fun getTotalMemory(context: Context): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem / (1024 * 1024)
    }
    
    /**
     * 获取可用内存大小（MB）
     */
    fun getAvailableMemory(context: Context): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.availMem / (1024 * 1024)
    }
    
    /**
     * 获取内存使用率
     */
    fun getMemoryUsagePercent(context: Context): Float {
        val total = getTotalMemory(context)
        val available = getAvailableMemory(context)
        return if (total > 0) {
            ((total - available).toFloat() / total) * 100
        } else {
            0f
        }
    }
    
    /**
     * 获取内部存储总空间（MB）
     */
    fun getInternalStorageTotal(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return (totalBlocks * blockSize) / (1024 * 1024)
    }
    
    /**
     * 获取内部存储可用空间（MB）
     */
    fun getInternalStorageAvailable(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return (availableBlocks * blockSize) / (1024 * 1024)
    }
    
    /**
     * 获取外部存储总空间（MB）
     */
    fun getExternalStorageTotal(): Long {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            (totalBlocks * blockSize) / (1024 * 1024)
        } else {
            0L
        }
    }
    
    /**
     * 获取外部存储可用空间（MB）
     */
    fun getExternalStorageAvailable(): Long {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val availableBlocks = stat.availableBlocksLong
            (availableBlocks * blockSize) / (1024 * 1024)
        } else {
            0L
        }
    }
    
    /**
     * 获取CPU架构
     */
    fun getCPUArchitecture(): String {
        return Build.SUPPORTED_ABIS.joinToString(", ")
    }
    
    /**
     * 获取CPU核心数
     */
    fun getCPUCoreCount(): Int {
        return Runtime.getRuntime().availableProcessors()
    }
    
    /**
     * 获取MAC地址
     */
    @SuppressLint("HardwareIds")
    fun getMacAddress(context: Context): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0以上获取MAC地址
                val interfaces = NetworkInterface.getNetworkInterfaces()
                for (networkInterface in interfaces) {
                    if (networkInterface.name.equals("wlan0", ignoreCase = true)) {
                        val mac = networkInterface.hardwareAddress
                        if (mac != null) {
                            return mac.joinToString(":") { "%02x".format(it) }
                        }
                    }
                }
                ""
            } else {
                // Android 6.0以下获取MAC地址
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiManager.connectionInfo.macAddress ?: ""
            }
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * 获取运营商名称
     */
    @SuppressLint("MissingPermission")
    fun getCarrierName(context: Context): String {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.networkOperatorName ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * 获取SIM卡序列号
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getSimSerialNumber(context: Context): String {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.simSerialNumber ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * 是否支持指定功能
     */
    fun hasSystemFeature(context: Context, feature: String): Boolean {
        return context.packageManager.hasSystemFeature(feature)
    }
    
    /**
     * 是否支持相机
     */
    fun hasCamera(context: Context): Boolean {
        return hasSystemFeature(context, PackageManager.FEATURE_CAMERA)
    }
    
    /**
     * 是否支持前置摄像头
     */
    fun hasFrontCamera(context: Context): Boolean {
        return hasSystemFeature(context, PackageManager.FEATURE_CAMERA_FRONT)
    }
    
    /**
     * 是否支持闪光灯
     */
    fun hasFlashlight(context: Context): Boolean {
        return hasSystemFeature(context, PackageManager.FEATURE_CAMERA_FLASH)
    }
    
    /**
     * 是否支持蓝牙
     */
    fun hasBluetooth(context: Context): Boolean {
        return hasSystemFeature(context, PackageManager.FEATURE_BLUETOOTH)
    }
    
    /**
     * 是否支持WiFi
     */
    fun hasWiFi(context: Context): Boolean {
        return hasSystemFeature(context, PackageManager.FEATURE_WIFI)
    }
    
    /**
     * 是否支持GPS
     */
    fun hasGPS(context: Context): Boolean {
        return hasSystemFeature(context, PackageManager.FEATURE_LOCATION_GPS)
    }
    
    /**
     * 是否支持指纹识别
     */
    fun hasFingerprint(context: Context): Boolean {
        return hasSystemFeature(context, PackageManager.FEATURE_FINGERPRINT)
    }
    
    /**
     * 获取设备信息摘要
     */
    fun getDeviceInfo(context: Context): DeviceInfo {
        return DeviceInfo(
            deviceId = getDeviceId(context),
            model = getDeviceModel(),
            brand = getDeviceBrand(),
            manufacturer = getDeviceManufacturer(),
            androidVersion = getAndroidVersion(),
            androidSDK = getAndroidSDK(),
            appVersionName = getAppVersionName(context),
            appVersionCode = getAppVersionCode(context),
            screenWidth = getScreenWidth(context),
            screenHeight = getScreenHeight(context),
            screenDensity = getScreenDensity(context),
            screenDPI = getScreenDPI(context),
            isTablet = isTablet(context),
            totalMemory = getTotalMemory(context),
            availableMemory = getAvailableMemory(context),
            internalStorageTotal = getInternalStorageTotal(),
            internalStorageAvailable = getInternalStorageAvailable(),
            externalStorageTotal = getExternalStorageTotal(),
            externalStorageAvailable = getExternalStorageAvailable(),
            cpuArchitecture = getCPUArchitecture(),
            cpuCoreCount = getCPUCoreCount(),
            macAddress = getMacAddress(context),
            carrierName = getCarrierName(context)
        )
    }
    
    /**
     * 设备信息数据类
     */
    data class DeviceInfo(
        val deviceId: String,
        val model: String,
        val brand: String,
        val manufacturer: String,
        val androidVersion: String,
        val androidSDK: Int,
        val appVersionName: String,
        val appVersionCode: Long,
        val screenWidth: Int,
        val screenHeight: Int,
        val screenDensity: Float,
        val screenDPI: Int,
        val isTablet: Boolean,
        val totalMemory: Long,
        val availableMemory: Long,
        val internalStorageTotal: Long,
        val internalStorageAvailable: Long,
        val externalStorageTotal: Long,
        val externalStorageAvailable: Long,
        val cpuArchitecture: String,
        val cpuCoreCount: Int,
        val macAddress: String,
        val carrierName: String
    )
}