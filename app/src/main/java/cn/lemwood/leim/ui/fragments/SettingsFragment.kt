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
import cn.lemwood.leim.utils.AutoStartPermissionHelper
import cn.lemwood.leim.utils.NotificationHelper
import cn.lemwood.leim.utils.PreferenceManager

/**
 * 设置页面 Fragment
 */
class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var autoStartHelper: AutoStartPermissionHelper
    
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
        notificationHelper = NotificationHelper(requireContext())
        autoStartHelper = AutoStartPermissionHelper(requireContext())
        
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
            if (isChecked) {
                // 开启自启动时检查权限
                if (autoStartHelper.isAutoStartPermissionRequired()) {
                    // 需要申请权限，先关闭开关
                    binding.switchAutoStart.isChecked = false
                    autoStartHelper.showAutoStartPermissionDialog()
                } else {
                    preferenceManager.setAutoStartEnabled(true)
                }
            } else {
                preferenceManager.setAutoStartEnabled(false)
            }
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
        
        binding.btnTestNotification.setOnClickListener {
            // 测试通知
            testNotification()
        }
        
        binding.btnAbout.setOnClickListener {
            // 关于页面
            Toast.makeText(context, "Leim v1.0\n一个简单的实时通信应用", Toast.LENGTH_LONG).show()
        }
        
        binding.btnLogout.setOnClickListener {
            performLogout()
        }
        
        binding.btnRequestAutoStartPermission.setOnClickListener {
            // 申请自启动权限
            autoStartHelper.showAutoStartPermissionDialog()
        }
    }
    
    /**
     * 加载设置
     */
    private fun loadSettings() {
        // 检查自启动权限状态
        val autoStartEnabled = preferenceManager.isAutoStartEnabled()
        val hasPermission = !autoStartHelper.isAutoStartPermissionRequired()
        
        // 控制权限申请按钮的显示
        if (!hasPermission) {
            binding.layoutAutoStartPermission.visibility = View.VISIBLE
        } else {
            binding.layoutAutoStartPermission.visibility = View.GONE
        }
        
        // 如果用户开启了自启动但没有权限，显示提示
        if (autoStartEnabled && !hasPermission) {
            binding.switchAutoStart.isChecked = false
            Toast.makeText(context, "请先授予自启动权限", Toast.LENGTH_SHORT).show()
        } else {
            binding.switchAutoStart.isChecked = autoStartEnabled && hasPermission
        }
        
        binding.switchNotification.isChecked = preferenceManager.isNotificationEnabled()
        binding.switchSound.isChecked = preferenceManager.isSoundEnabled()
        binding.switchVibration.isChecked = preferenceManager.isVibrationEnabled()
    }
    
    /**
     * 测试通知功能
     */
    private fun testNotification() {
        if (!preferenceManager.isNotificationEnabled()) {
            Toast.makeText(context, "请先开启消息通知", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 显示测试通知
        notificationHelper.showMessageNotification(
            title = "测试通知",
            content = "这是一条测试消息，用于验证声音和振动功能是否正常工作。",
            conversationId = "test",
            senderId = "system"
        )
        
        Toast.makeText(context, "已发送测试通知", Toast.LENGTH_SHORT).show()
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
    
    override fun onResume() {
        super.onResume()
        // 重新检查权限状态，用户可能从设置页面返回
        updateAutoStartStatus()
    }
    
    /**
     * 更新自启动状态
     */
    private fun updateAutoStartStatus() {
        val autoStartEnabled = preferenceManager.isAutoStartEnabled()
        val hasPermission = !autoStartHelper.isAutoStartPermissionRequired()
        
        // 控制权限申请按钮的显示
        if (!hasPermission) {
            binding.layoutAutoStartPermission.visibility = View.VISIBLE
        } else {
            binding.layoutAutoStartPermission.visibility = View.GONE
        }
        
        // 如果用户已经授予权限且之前开启了自启动，则更新开关状态
        if (autoStartEnabled && hasPermission) {
            binding.switchAutoStart.isChecked = true
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}