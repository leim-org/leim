package cn.lemwood.leim

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import cn.lemwood.leim.databinding.ActivityMainBinding
import cn.lemwood.leim.services.WebSocketService
import cn.lemwood.leim.ui.activities.LoginActivity
import cn.lemwood.leim.ui.viewmodels.MainViewModel
import cn.lemwood.leim.utils.PreferenceManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
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
        
        setupNavigation()
        startWebSocketService()
        observeViewModel()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)
    }
    
    private fun startWebSocketService() {
        val intent = Intent(this, WebSocketService::class.java)
        startForegroundService(intent)
    }
    
    private fun observeViewModel() {
        viewModel.currentUser.observe(this) { user ->
            // 更新用户信息显示
        }
        
        viewModel.connectionStatus.observe(this) { isConnected ->
            // 更新连接状态显示
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 不停止WebSocket服务，让它在后台继续运行
    }
}