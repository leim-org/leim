package cn.lemwood.leim.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import java.io.File
import kotlin.system.exitProcess

/**
 * 应用工具类
 * 提供应用生命周期管理、状态监控等功能
 */
object AppUtils : LifecycleObserver {
    
    private var application: Application? = null
    private var isAppInForeground = false
    private var isAppInitialized = false
    private val activityStack = mutableListOf<Activity>()
    
    // 应用状态监听器
    private val appStateListeners = mutableListOf<AppStateListener>()
    
    /**
     * 初始化应用工具类
     */
    fun init(app: Application) {
        if (isAppInitialized) return
        
        application = app
        isAppInitialized = true
        
        // 注册生命周期监听
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        
        // 注册Activity生命周期回调
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                addActivity(activity)
                LogUtils.d("Activity created: ${activity.javaClass.simpleName}")
            }
            
            override fun onActivityStarted(activity: Activity) {
                LogUtils.d("Activity started: ${activity.javaClass.simpleName}")
            }
            
            override fun onActivityResumed(activity: Activity) {
                LogUtils.d("Activity resumed: ${activity.javaClass.simpleName}")
            }
            
            override fun onActivityPaused(activity: Activity) {
                LogUtils.d("Activity paused: ${activity.javaClass.simpleName}")
            }
            
            override fun onActivityStopped(activity: Activity) {
                LogUtils.d("Activity stopped: ${activity.javaClass.simpleName}")
            }
            
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                LogUtils.d("Activity save instance state: ${activity.javaClass.simpleName}")
            }
            
            override fun onActivityDestroyed(activity: Activity) {
                removeActivity(activity)
                LogUtils.d("Activity destroyed: ${activity.javaClass.simpleName}")
            }
        })
        
        LogUtils.i("AppUtils initialized successfully")
    }
    
    /**
     * 应用进入前台
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForeground() {
        isAppInForeground = true
        LogUtils.i("App entered foreground")
        notifyAppStateChanged(true)
    }
    
    /**
     * 应用进入后台
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackground() {
        isAppInForeground = false
        LogUtils.i("App entered background")
        notifyAppStateChanged(false)
    }
    
    /**
     * 获取应用实例
     */
    fun getApplication(): Application {
        return application ?: throw IllegalStateException("AppUtils not initialized")
    }
    
    /**
     * 获取应用上下文
     */
    fun getContext(): Context {
        return getApplication().applicationContext
    }
    
    /**
     * 判断应用是否在前台
     */
    fun isAppInForeground(): Boolean {
        return isAppInForeground
    }
    
    /**
     * 判断应用是否在后台
     */
    fun isAppInBackground(): Boolean {
        return !isAppInForeground
    }
    
    /**
     * 添加Activity到栈中
     */
    private fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }
    
    /**
     * 从栈中移除Activity
     */
    private fun removeActivity(activity: Activity) {
        activityStack.remove(activity)
    }
    
    /**
     * 获取当前Activity
     */
    fun getCurrentActivity(): Activity? {
        return activityStack.lastOrNull()
    }
    
    /**
     * 获取栈顶Activity
     */
    fun getTopActivity(): Activity? {
        return activityStack.lastOrNull { !it.isFinishing }
    }
    
    /**
     * 获取Activity栈大小
     */
    fun getActivityStackSize(): Int {
        return activityStack.size
    }
    
    /**
     * 结束指定Activity
     */
    fun finishActivity(activity: Activity) {
        if (!activity.isFinishing) {
            activity.finish()
        }
    }
    
    /**
     * 结束指定类型的Activity
     */
    fun finishActivity(clazz: Class<out Activity>) {
        activityStack.filter { it.javaClass == clazz }.forEach { finishActivity(it) }
    }
    
    /**
     * 结束所有Activity
     */
    fun finishAllActivities() {
        activityStack.toList().forEach { finishActivity(it) }
    }
    
    /**
     * 结束除指定Activity外的所有Activity
     */
    fun finishAllActivitiesExcept(clazz: Class<out Activity>) {
        activityStack.filter { it.javaClass != clazz }.forEach { finishActivity(it) }
    }
    
    /**
     * 退出应用
     */
    fun exitApp() {
        try {
            finishAllActivities()
            Process.killProcess(Process.myPid())
            exitProcess(0)
        } catch (e: Exception) {
            LogUtils.e("Failed to exit app", e)
        }
    }
    
    /**
     * 重启应用
     */
    fun restartApp() {
        try {
            val context = getContext()
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            }
            exitApp()
        } catch (e: Exception) {
            LogUtils.e("Failed to restart app", e)
        }
    }
    
    /**
     * 获取应用包名
     */
    fun getPackageName(): String {
        return getContext().packageName
    }
    
    /**
     * 获取应用名称
     */
    fun getAppName(): String {
        return try {
            val context = getContext()
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            LogUtils.e("Failed to get app name", e)
            ""
        }
    }
    
    /**
     * 获取应用版本名
     */
    fun getVersionName(): String {
        return try {
            val context = getContext()
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: ""
        } catch (e: Exception) {
            LogUtils.e("Failed to get version name", e)
            ""
        }
    }
    
    /**
     * 获取应用版本号
     */
    fun getVersionCode(): Long {
        return try {
            val context = getContext()
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            LogUtils.e("Failed to get version code", e)
            0L
        }
    }
    
    /**
     * 获取应用签名
     */
    fun getAppSignature(): String {
        return try {
            val context = getContext()
            val packageManager = context.packageManager
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }
            
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }
            
            signatures?.firstOrNull()?.toCharsString() ?: ""
        } catch (e: Exception) {
            LogUtils.e("Failed to get app signature", e)
            ""
        }
    }
    
    /**
     * 判断应用是否为Debug版本
     */
    fun isDebug(): Boolean {
        return try {
            val context = getContext()
            val applicationInfo = context.applicationInfo
            (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 判断应用是否安装
     */
    fun isAppInstalled(packageName: String): Boolean {
        return try {
            val context = getContext()
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    /**
     * 启动其他应用
     */
    fun launchApp(packageName: String): Boolean {
        return try {
            val context = getContext()
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            intent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
                true
            } ?: false
        } catch (e: Exception) {
            LogUtils.e("Failed to launch app: $packageName", e)
            false
        }
    }
    
    /**
     * 卸载应用
     */
    fun uninstallApp(packageName: String) {
        try {
            val context = getContext()
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            LogUtils.e("Failed to uninstall app: $packageName", e)
        }
    }
    
    /**
     * 打开应用设置页面
     */
    fun openAppSettings() {
        try {
            val context = getContext()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            LogUtils.e("Failed to open app settings", e)
        }
    }
    
    /**
     * 获取应用安装时间
     */
    fun getInstallTime(): Long {
        return try {
            val context = getContext()
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.firstInstallTime
        } catch (e: Exception) {
            LogUtils.e("Failed to get install time", e)
            0L
        }
    }
    
    /**
     * 获取应用更新时间
     */
    fun getUpdateTime(): Long {
        return try {
            val context = getContext()
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.lastUpdateTime
        } catch (e: Exception) {
            LogUtils.e("Failed to get update time", e)
            0L
        }
    }
    
    /**
     * 获取应用APK路径
     */
    fun getApkPath(): String {
        return try {
            val context = getContext()
            val applicationInfo = context.applicationInfo
            applicationInfo.sourceDir
        } catch (e: Exception) {
            LogUtils.e("Failed to get APK path", e)
            ""
        }
    }
    
    /**
     * 获取应用APK大小
     */
    fun getApkSize(): Long {
        return try {
            val apkPath = getApkPath()
            if (apkPath.isNotEmpty()) {
                File(apkPath).length()
            } else {
                0L
            }
        } catch (e: Exception) {
            LogUtils.e("Failed to get APK size", e)
            0L
        }
    }
    
    /**
     * 判断应用是否在运行
     */
    fun isAppRunning(packageName: String): Boolean {
        return try {
            val context = getContext()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = activityManager.runningAppProcesses
            runningApps?.any { it.processName == packageName } ?: false
        } catch (e: Exception) {
            LogUtils.e("Failed to check if app is running: $packageName", e)
            false
        }
    }
    
    /**
     * 清理应用缓存
     */
    fun clearAppCache() {
        try {
            val context = getContext()
            val cacheDir = context.cacheDir
            deleteDir(cacheDir)
            LogUtils.i("App cache cleared")
        } catch (e: Exception) {
            LogUtils.e("Failed to clear app cache", e)
        }
    }
    
    /**
     * 删除目录及其内容
     */
    private fun deleteDir(dir: File?): Boolean {
        return try {
            if (dir != null && dir.isDirectory) {
                val children = dir.list()
                children?.forEach { child ->
                    val success = deleteDir(File(dir, child))
                    if (!success) return false
                }
            }
            dir?.delete() ?: false
        } catch (e: Exception) {
            LogUtils.e("Failed to delete directory: ${dir?.absolutePath}", e)
            false
        }
    }
    
    /**
     * 添加应用状态监听器
     */
    fun addAppStateListener(listener: AppStateListener) {
        if (!appStateListeners.contains(listener)) {
            appStateListeners.add(listener)
        }
    }
    
    /**
     * 移除应用状态监听器
     */
    fun removeAppStateListener(listener: AppStateListener) {
        appStateListeners.remove(listener)
    }
    
    /**
     * 通知应用状态变化
     */
    private fun notifyAppStateChanged(isInForeground: Boolean) {
        appStateListeners.forEach { listener ->
            try {
                if (isInForeground) {
                    listener.onAppForeground()
                } else {
                    listener.onAppBackground()
                }
            } catch (e: Exception) {
                LogUtils.e("Error in app state listener", e)
            }
        }
    }
    
    /**
     * 应用状态监听接口
     */
    interface AppStateListener {
        /**
         * 应用进入前台
         */
        fun onAppForeground()
        
        /**
         * 应用进入后台
         */
        fun onAppBackground()
    }
    
    /**
     * 简化的应用状态监听器
     */
    abstract class SimpleAppStateListener : AppStateListener {
        override fun onAppForeground() {}
        override fun onAppBackground() {}
    }
    
    /**
     * 应用信息数据类
     */
    data class AppInfo(
        val packageName: String,
        val appName: String,
        val versionName: String,
        val versionCode: Long,
        val installTime: Long,
        val updateTime: Long,
        val apkPath: String,
        val apkSize: Long,
        val isDebug: Boolean,
        val signature: String
    )
    
    /**
     * 获取完整应用信息
     */
    fun getAppInfo(): AppInfo {
        return AppInfo(
            packageName = getPackageName(),
            appName = getAppName(),
            versionName = getVersionName(),
            versionCode = getVersionCode(),
            installTime = getInstallTime(),
            updateTime = getUpdateTime(),
            apkPath = getApkPath(),
            apkSize = getApkSize(),
            isDebug = isDebug(),
            signature = getAppSignature()
        )
    }
}