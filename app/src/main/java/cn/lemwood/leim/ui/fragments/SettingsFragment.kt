package cn.lemwood.leim.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.lemwood.leim.databinding.FragmentSettingsBinding
import cn.lemwood.leim.ui.activities.LoginActivity
import cn.lemwood.leim.utils.PreferenceManager

/**
 * 设置页面 Fragment
 */
class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferenceManager = PreferenceManager(requireContext())
        
        setupViews()
        loadSettings()
    }
    
    /**
     * 设置视图
     */
    private fun setupViews() {
        // 用户信息
        binding.textViewUserId.text = "Leim 号: ${preferenceManager.getUserId() ?: "未知"}"
        binding.textViewNickname.text = preferenceManager.getUserNickname() ?: "未设置昵称"
        
        // 设置开关监听器
        binding.switchAutoStart.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.setAutoStartEnabled(isChecked)
        }
        
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.setNotificationEnabled(isChecked)
        }
        
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.setSoundEnabled(isChecked)
        }
        
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.setVibrationEnabled(isChecked)
        }
        
        // 按钮点击事件
        binding.btnEditProfile.setOnClickListener {
            // 编辑个人资料
            Toast.makeText(context, "编辑个人资料功能正在开发中", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnAbout.setOnClickListener {
            // 关于页面
            Toast.makeText(context, "Leim v1.0\n一个简单的实时通信应用", Toast.LENGTH_LONG).show()
        }
        
        binding.btnLogout.setOnClickListener {
            performLogout()
        }
    }
    
    /**
     * 加载设置
     */
    private fun loadSettings() {
        binding.switchAutoStart.isChecked = preferenceManager.isAutoStartEnabled()
        binding.switchNotification.isChecked = preferenceManager.isNotificationEnabled()
        binding.switchSound.isChecked = preferenceManager.isSoundEnabled()
        binding.switchVibration.isChecked = preferenceManager.isVibrationEnabled()
    }
    
    /**
     * 执行退出登录
     */
    private fun performLogout() {
        preferenceManager.clear()
        
        Toast.makeText(context, "已退出登录", Toast.LENGTH_SHORT).show()
        
        // 跳转到登录页面
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        
        requireActivity().finish()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}