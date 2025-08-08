package cn.lemwood.leim.utils

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast

/**
 * 自启动权限管理助手
 * 处理不同厂商的自启动权限申请
 */
class AutoStartPermissionHelper(private val context: Context) {
    
    companion object {
        private const val TAG = "AutoStartPermission"
        
        // 各厂商的自启动设置页面
        private val AUTOSTART_INTENTS = arrayOf(
            // 华为
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            
            // 小米
            Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            Intent().setComponent(ComponentName("com.xiaomi.mipicks", "com.xiaomi.mipicks.ui.AppPicksTabActivity")),
            
            // OPPO
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.FakeActivity")),
            Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")),
            
            // VIVO
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            
            // 魅族
            Intent().setComponent(ComponentName("com.meizu.safe", "com.meizu.safe.security.SHOW_APPSEC")),
            
            // 一加
            Intent().setComponent(ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity")),
            
            // 联想
            Intent().setComponent(ComponentName("com.lenovo.security", "com.lenovo.security.purebackground.PureBackgroundActivity")),
            
            // 三星
            Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            
            // 锤子
            Intent().setComponent(ComponentName("com.smartisanos.security", "com.smartisanos.security.invokeHistory.InvokeHistoryActivity"))
        )
    }
    
    /**
     * 检查是否需要申请自启动权限
     */
    fun isAutoStartPermissionRequired(): Boolean {
        return !isIgnoringBatteryOptimizations() || !canAutoStart()
    }
    
    /**
     * 检查是否已忽略电池优化
     */
    fun isIgnoringBatteryOptimizations(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true // Android 6.0 以下版本不需要此权限
        }
    }
    
    /**
     * 检查是否可以自启动（简单检测）
     */
    private fun canAutoStart(): Boolean {
        // 这里可以添加更复杂的检测逻辑
        // 目前简单返回 true，实际使用中可以根据厂商进行特定检测
        return true
    }
    
    /**
     * 申请忽略电池优化权限
     */
    fun requestIgnoreBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isIgnoringBatteryOptimizations()) {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    intent.data = Uri.parse("package:${context.packageName}")
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "无法打开电池优化设置", e)
                    // 如果无法直接跳转，则跳转到电池优化列表
                    try {
                        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        context.startActivity(intent)
                        Toast.makeText(context, "请在列表中找到 Leim 并允许忽略电池优化", Toast.LENGTH_LONG).show()
                    } catch (e2: Exception) {
                        Log.e(TAG, "无法打开电池优化设置页面", e2)
                        Toast.makeText(context, "请手动在设置中关闭 Leim 的电池优化", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    
    /**
     * 尝试打开自启动设置页面
     */
    fun openAutoStartSettings(): Boolean {
        for (intent in AUTOSTART_INTENTS) {
            try {
                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    context.startActivity(intent)
                    return true
                }
            } catch (e: Exception) {
                Log.d(TAG, "无法打开自启动设置: ${intent.component}", e)
            }
        }
        
        // 如果都无法打开，尝试打开应用详情页
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "无法打开应用详情页", e)
        }
        
        return false
    }
    
    /**
     * 显示自启动权限申请对话框
     */
    fun showAutoStartPermissionDialog() {
        val deviceHint = getPermissionHint()
        val batteryOptimizationStatus = if (isIgnoringBatteryOptimizations()) "✓ 已开启" else "✗ 未开启"
        
        AlertDialog.Builder(context)
            .setTitle("开启自启动权限")
            .setMessage("""
                为了确保 Leim 能够在后台持续接收消息，需要开启以下权限：
                
                📱 当前设备：${Build.BRAND} ${Build.MODEL}
                🔋 电池优化：$batteryOptimizationStatus
                
                📋 设置步骤：
                $deviceHint
                
                点击"去设置"将自动跳转到相应的设置页面。
            """.trimIndent())
            .setPositiveButton("去设置") { _, _ ->
                requestAutoStartPermissions()
            }
            .setNegativeButton("稍后设置") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("查看帮助") { _, _ ->
                showDetailedHelp()
            }
            .setCancelable(false)
            .show()
    }
    
    /**
     * 申请自启动相关权限
     */
    fun requestAutoStartPermissions() {
        // 1. 先申请忽略电池优化
        if (!isIgnoringBatteryOptimizations()) {
            requestIgnoreBatteryOptimizations()
            return
        }
        
        // 2. 尝试打开自启动设置
        if (!openAutoStartSettings()) {
            Toast.makeText(context, "请在系统设置中手动开启 Leim 的自启动权限", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "请在设置中允许 Leim 自启动", Toast.LENGTH_LONG).show()
        }
    }
    
    /**
     * 获取当前设备厂商
     */
    fun getDeviceBrand(): String {
        return Build.BRAND.lowercase()
    }
    
    /**
     * 获取针对当前设备的权限设置提示
     */
    fun getPermissionHint(): String {
        return when (getDeviceBrand()) {
            "huawei", "honor" -> "请在「手机管家」-「启动管理」中允许 Leim 自启动"
            "xiaomi", "redmi" -> "请在「安全中心」-「授权管理」-「自启动管理」中允许 Leim 自启动"
            "oppo" -> "请在「手机管家」-「权限隐私」-「自启动管理」中允许 Leim 自启动"
            "vivo", "iqoo" -> "请在「i管家」-「应用管理」-「自启动」中允许 Leim 自启动"
            "meizu" -> "请在「手机管家」-「权限管理」-「后台管理」中允许 Leim 自启动"
            "oneplus" -> "请在「设置」-「电池」-「电池优化」中关闭 Leim 的优化"
            "samsung" -> "请在「设备维护」-「电池」-「应用电源管理」中关闭 Leim 的优化"
            else -> "请在系统设置中允许 Leim 自启动并关闭电池优化"
        }
    }
    
    /**
     * 显示详细帮助信息
     */
    private fun showDetailedHelp() {
        val brand = getDeviceBrand()
        val detailedSteps = when (brand) {
            "huawei", "honor" -> """
                华为/荣耀设备设置步骤：
                
                1️⃣ 自启动管理：
                • 打开「手机管家」
                • 点击「启动管理」
                • 找到「Leim」并开启自启动
                
                2️⃣ 后台应用保护：
                • 在「手机管家」中点击「应用启动管理」
                • 找到「Leim」，关闭「自动管理」
                • 手动开启「允许自启动」、「允许关联启动」、「允许后台活动」
                
                3️⃣ 电池优化：
                • 打开「设置」-「电池」-「更多电池设置」
                • 点击「休眠时始终保持网络连接」
            """.trimIndent()
            
            "xiaomi", "redmi" -> """
                小米/红米设备设置步骤：
                
                1️⃣ 自启动管理：
                • 打开「安全中心」
                • 点击「授权管理」-「自启动管理」
                • 找到「Leim」并开启自启动
                
                2️⃣ 后台保护：
                • 在「安全中心」中点击「应用管理」
                • 找到「Leim」，开启「后台弹出界面」
                • 开启「显示悬浮窗」
                
                3️⃣ 省电策略：
                • 打开「设置」-「省电与电池」-「应用智能省电」
                • 找到「Leim」，选择「无限制」
            """.trimIndent()
            
            "oppo" -> """
                OPPO设备设置步骤：
                
                1️⃣ 自启动管理：
                • 打开「手机管家」
                • 点击「权限隐私」-「自启动管理」
                • 找到「Leim」并开启自启动
                
                2️⃣ 后台冻结：
                • 在「手机管家」中点击「应用管理」
                • 找到「Leim」，关闭「后台冻结」
                
                3️⃣ 电池优化：
                • 打开「设置」-「电池」-「高耗电应用优化」
                • 找到「Leim」，选择「不优化」
            """.trimIndent()
            
            "vivo", "iqoo" -> """
                VIVO/iQOO设备设置步骤：
                
                1️⃣ 自启动管理：
                • 打开「i管家」
                • 点击「应用管理」-「自启动」
                • 找到「Leim」并开启自启动
                
                2️⃣ 后台高耗电：
                • 在「i管家」中点击「省电管理」
                • 点击「后台高耗电」
                • 找到「Leim」并允许后台高耗电
                
                3️⃣ 电池白名单：
                • 打开「设置」-「电池」-「后台应用管理」
                • 找到「Leim」，选择「允许后台活动」
            """.trimIndent()
            
            else -> """
                通用设置步骤：
                
                1️⃣ 电池优化：
                • 打开「设置」-「电池」-「电池优化」
                • 找到「Leim」，选择「不优化」
                
                2️⃣ 应用权限：
                • 打开「设置」-「应用管理」
                • 找到「Leim」-「权限」
                • 开启所有必要权限
                
                3️⃣ 后台限制：
                • 在应用详情中查看「电池」设置
                • 关闭「后台应用刷新限制」
            """.trimIndent()
        }
        
        AlertDialog.Builder(context)
            .setTitle("详细设置指南")
            .setMessage(detailedSteps)
            .setPositiveButton("我知道了") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("去设置") { _, _ ->
                requestAutoStartPermissions()
            }
            .show()
    }
}