package cn.lemwood.leim.ui.activities

import android.content.Intent
import android.os.Bundle
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
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferenceManager = PreferenceManager(this)
        
        // 检查登录状态
        if (!preferenceManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        requestPermissions()
        startWebSocketService()
        
        // 默认显示消息页面
        if (savedInstanceState == null) {
            replaceFragment(MessageFragment())
        }
    }
    
    /**
     * 设置底部导航
     */
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_messages -> {
                    replaceFragment(MessageFragment())
                    true
                }
                R.id.nav_contacts -> {
                    replaceFragment(ContactFragment())
                    true
                }
                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    /**
     * 替换 Fragment
     */
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    /**
     * 请求权限
     */
    private fun requestPermissions() {
        if (!PermissionHelper.hasAllPermissions(this)) {
            PermissionHelper.requestPermissions(this)
        }
    }
    
    /**
     * 启动 WebSocket 服务
     */
    private fun startWebSocketService() {
        val serviceIntent = Intent(this, WebSocketService::class.java)
        startForegroundService(serviceIntent)
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        PermissionHelper.onRequestPermissionsResult(
            requestCode = requestCode,
            permissions = permissions,
            grantResults = grantResults,
            onAllGranted = {
                // 所有权限已授予
            },
            onDenied = { deniedPermissions ->
                // 部分权限被拒绝
                // 可以显示说明对话框
            }
        )
    }
}