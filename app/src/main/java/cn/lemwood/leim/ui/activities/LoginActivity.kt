package cn.lemwood.leim.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.lemwood.leim.databinding.ActivityLoginBinding
import cn.lemwood.leim.utils.PreferenceManager

/**
 * 登录活动
 */
class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferenceManager = PreferenceManager(this)
        
        setupViews()
    }
    
    /**
     * 设置视图
     */
    private fun setupViews() {
        binding.buttonLogin.setOnClickListener {
            performLogin()
        }
    }
    
    /**
     * 执行登录
     */
    private fun performLogin() {
        val leimId = binding.editTextLeimId.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        
        if (leimId.isEmpty()) {
            binding.editTextLeimId.error = "请输入 Leim 号"
            return
        }
        
        if (password.isEmpty()) {
            binding.editTextPassword.error = "请输入密码"
            return
        }
        
        // 模拟登录验证（因为后端服务器正在施工）
        if (leimId.length >= 3 && password.length >= 6) {
            // 登录成功
            preferenceManager.setLoggedIn(true)
            preferenceManager.setUserId(leimId)
            preferenceManager.setUserNickname("用户$leimId")
            
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
            
            // 跳转到主界面
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Leim 号至少3位，密码至少6位", Toast.LENGTH_SHORT).show()
        }
    }
}