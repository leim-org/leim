package cn.lemwood.leim.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import cn.lemwood.leim.R
import cn.lemwood.leim.databinding.ActivityMainBinding
import cn.lemwood.leim.services.WebSocketService
import cn.lemwood.leim.ui.fragments.ContactFragment
import cn.lemwood.leim.ui.fragments.MessageFragment
import cn.lemwood.leim.ui.fragments.SettingsFragment
import cn.lemwood.leim.utils.PermissionHelper
import cn.lemwood.leim.utils.PreferenceManager

/**
 * 主活动
 */
class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "MainActivity onCreate 开始")
        
        try {
            preferenceManager = PreferenceManager(this)
            
            // 检查登录状态
            val isLoggedIn = preferenceManager.isLoggedIn()
            Log.d(TAG, "登录状态: $isLoggedIn")
            
            if (!isLoggedIn) {
                Log.d(TAG, "用户未登录，跳转到登录页面")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }
            
            Log.d(TAG, "开始初始化视图")
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            Log.d(TAG, "设置底部导航")
            setupBottomNavigation()
            
            Log.d(TAG, "请求权限")
            requestPermissions()
            
            Log.d(TAG, "启动WebSocket服务")
            startWebSocketService()
            
            // 默认显示消息页面
            if (savedInstanceState == null) {
                Log.d(TAG, "显示默认消息页面")
                replaceFragment(MessageFragment())
                // 设置底部导航选中状态
                binding.bottomNavigation.selectedItemId = R.id.nav_messages
            }
            
            Log.d(TAG, "MainActivity onCreate 完成")
            
        } catch (e: Exception) {
            Log.e(TAG, "MainActivity onCreate 发生错误", e)
            // 记录崩溃日志
            cn.lemwood.leim.utils.CrashLogger.logException(e, "MainActivity_onCreate")
            
            // 跳转到测试页面
            try {
                startActivity(Intent(this, TestActivity::class.java))
                finish()
            } catch (testException: Exception) {
                Log.e(TAG, "无法启动测试页面", testException)
            }
        }
    }
    
    /**
     * 设置底部导航
     */
    private fun setupBottomNavigation() {
        try {
            Log.d(TAG, "设置底部导航监听器")
            binding.bottomNavigation.setOnItemSelectedListener { item ->
                Log.d(TAG, "底部导航项被点击: ${item.itemId}")
                when (item.itemId) {
                    R.id.nav_messages -> {
                        Log.d(TAG, "切换到消息页面")
                        replaceFragment(MessageFragment())
                        true
                    }
                    R.id.nav_contacts -> {
                        Log.d(TAG, "切换到联系人页面")
                        replaceFragment(ContactFragment())
                        true
                    }
                    R.id.nav_settings -> {
                        Log.d(TAG, "切换到设置页面")
                        replaceFragment(SettingsFragment())
                        true
                    }
                    else -> {
                        Log.w(TAG, "未知的导航项: ${item.itemId}")
                        false
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "设置底部导航失败", e)
            cn.lemwood.leim.utils.CrashLogger.logException(e, "MainActivity_setupBottomNavigation")
        }
    }
    
    /**
     * 替换 Fragment
     */
    private fun replaceFragment(fragment: Fragment) {
        try {
            Log.d(TAG, "替换Fragment: ${fragment.javaClass.simpleName}")
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            Log.d(TAG, "Fragment替换成功")
        } catch (e: Exception) {
            Log.e(TAG, "替换Fragment失败", e)
            cn.lemwood.leim.utils.CrashLogger.logException(e, "MainActivity_replaceFragment")
        }
    }
    
    /**
     * 请求权限
     */
    private fun requestPermissions() {
        try {
            if (!PermissionHelper.hasAllPermissions(this)) {
                Log.d(TAG, "需要请求权限")
                PermissionHelper.requestPermissions(this)
            } else {
                Log.d(TAG, "所有权限已授予")
            }
        } catch (e: Exception) {
            Log.e(TAG, "请求权限失败", e)
            cn.lemwood.leim.utils.CrashLogger.logException(e, "MainActivity_requestPermissions")
        }
    }
    
    /**
     * 启动 WebSocket 服务
     */
    private fun startWebSocketService() {
        try {
            val serviceIntent = Intent(this, WebSocketService::class.java)
            
            // API 级别兼容性检查
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "启动前台服务")
                startForegroundService(serviceIntent)
            } else {
                Log.d(TAG, "启动普通服务")
                startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "启动WebSocket服务失败", e)
            cn.lemwood.leim.utils.CrashLogger.logException(e, "MainActivity_startWebSocketService")
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        try {
            PermissionHelper.onRequestPermissionsResult(
                requestCode = requestCode,
                permissions = permissions,
                grantResults = grantResults,
                onAllGranted = {
                    Log.d(TAG, "所有权限已授予")
                },
                onDenied = { deniedPermissions ->
                    Log.w(TAG, "部分权限被拒绝: ${deniedPermissions.joinToString()}")
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "处理权限结果失败", e)
            cn.lemwood.leim.utils.CrashLogger.logException(e, "MainActivity_onRequestPermissionsResult")
        }
    }
}