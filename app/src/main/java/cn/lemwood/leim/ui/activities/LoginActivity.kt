package cn.lemwood.leim.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.lemwood.leim.MainActivity
import cn.lemwood.leim.databinding.ActivityLoginBinding
import cn.lemwood.leim.ui.viewmodels.AuthViewModel
import cn.lemwood.leim.utils.PreferenceManager

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var preferenceManager: PreferenceManager
    private var isPasswordVisible = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferenceManager = PreferenceManager(this)
        
        // 如果已经登录，直接跳转到主页
        if (preferenceManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViews()
        observeViewModel()
    }
    
    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            val leimId = binding.etLeimId.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (validateInput(leimId, password)) {
                viewModel.login(leimId, password)
            }
        }
        
        binding.btnRegister.setOnClickListener {
            val leimId = binding.etLeimId.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (validateInput(leimId, password)) {
                viewModel.register(leimId, password)
            }
        }
        
        binding.ivPasswordToggle.setOnClickListener {
            togglePasswordVisibility()
        }
    }
    
    private fun validateInput(leimId: String, password: String): Boolean {
        if (leimId.isEmpty()) {
            binding.tilLeimId.error = "请输入Leim号"
            return false
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "请输入密码"
            return false
        }
        
        if (password.length < 6) {
            binding.tilPassword.error = "密码长度至少6位"
            return false
        }
        
        binding.tilLeimId.error = null
        binding.tilPassword.error = null
        return true
    }
    
    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            binding.etPassword.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.ivPasswordToggle.setImageResource(cn.lemwood.leim.R.drawable.ic_visibility_off)
        } else {
            binding.etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivPasswordToggle.setImageResource(cn.lemwood.leim.R.drawable.ic_visibility)
        }
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }
    
    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { user ->
                // 保存用户信息
                preferenceManager.setLoggedIn(true)
                preferenceManager.setCurrentUser(user)
                
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                
                // 跳转到主页
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.onFailure { error ->
                Toast.makeText(this, "登录失败: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
            binding.btnRegister.isEnabled = !isLoading
        }
    }
}