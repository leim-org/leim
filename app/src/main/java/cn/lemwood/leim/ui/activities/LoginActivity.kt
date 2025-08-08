package cn.lemwood.leim.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.lemwood.leim.databinding.ActivityLoginBinding
import cn.lemwood.leim.utils.PreferenceManager

/**
 * 登录活动
 */
class LoginActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "LoginActivity"
    }
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "LoginActivity onCreate 开始")
        
        try {
            preferenceManager = PreferenceManager(this)
            
            // 检查是否已登录
            if (preferenceManager.isLoggedIn()) {
                Log.d(TAG, "用户已登录，直接跳转到主页")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return
            }
            
            Log.d(TAG, "用户未登录，显示登录界面")
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            setupViews()
            
            Log.d(TAG, "LoginActivity onCreate 完成")
            
        } catch (e: Exception) {
            Log.e(TAG, "LoginActivity onCreate 发生错误", e)
            cn.lemwood.leim.utils.CrashLogger.logException(e, "LoginActivity_onCreate")
            
            // 如果登录页面也出错，尝试跳转到测试页面
            try {
                startActivity(Intent(this, TestActivity::class.java))
                finish()
            } catch (testException: Exception) {
                Log.e(TAG, "无法启动测试页面", testException)
            }
        }
    }
    
    /**
     * 设置视图
     */
    private fun setupViews() {
        try {
            Log.d(TAG, "设置登录按钮监听器")
            binding.buttonLogin.setOnClickListener {
                performLogin()
            }
        } catch (e: Exception) {
            Log.e(TAG, "设置视图失败", e)
            cn.lemwood.leim.utils.CrashLogger.logException(e, "LoginActivity_setupViews")
        }
    }
    
    /**
     * 执行登录
     */
    private fun performLogin() {
        try {
            Log.d(TAG, "开始执行登录")
            
            val leimId = binding.editTextLeimId.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val nickname = binding.editTextNickname.text.toString().trim()
            
            Log.d(TAG, "输入验证 - Leim号长度: ${leimId.length}, 密码长度: ${password.length}, 昵称长度: ${nickname.length}")
            
            if (leimId.isEmpty()) {
                binding.textInputLayoutLeimId.error = "请输入 Leim 号"
                return
            }
            
            if (password.isEmpty()) {
                binding.textInputLayoutPassword.error = "请输入密码"
                return
            }
            
            if (nickname.isEmpty()) {
                binding.textInputLayoutNickname.error = "请输入昵称"
                return
            }
            
            // 清除之前的错误信息
            binding.textInputLayoutLeimId.error = null
            binding.textInputLayoutPassword.error = null
            binding.textInputLayoutNickname.error = null
            
            // 模拟登录验证（因为后端服务器正在施工）
            if (leimId.length >= 3 && password.length >= 6) {
                Log.d(TAG, "登录验证成功，保存用户信息")
                
                // 登录成功
                preferenceManager.setLoggedIn(true)
                preferenceManager.setUserId(leimId)
                preferenceManager.setUserNickname(nickname)
                
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                
                Log.d(TAG, "跳转到主界面")
                // 跳转到主界面
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Log.w(TAG, "登录验证失败 - Leim号或密码长度不足")
                Toast.makeText(this, "Leim 号至少3位，密码至少6位", Toast.LENGTH_SHORT).show()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "执行登录失败", e)
            cn.lemwood.leim.utils.CrashLogger.logException(e, "LoginActivity_performLogin")
            Toast.makeText(this, "登录过程中发生错误", Toast.LENGTH_SHORT).show()
        }
    }
}