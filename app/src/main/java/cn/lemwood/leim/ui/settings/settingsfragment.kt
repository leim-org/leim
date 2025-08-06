package cn.lemwood.leim.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.lemwood.leim.databinding.FragmentSettingsBinding
import cn.lemwood.leim.ui.auth.LoginActivity
import cn.lemwood.leim.utils.PreferenceManager

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
    
    private fun setupViews() {
        // 用户信息
        val userInfo = preferenceManager.getUserInfo()
        if (userInfo != null) {
            binding.tvUserNickname.text = userInfo.nickname
            binding.tvUserLeimId.text = "Leim号: ${userInfo.leimId}"
            binding.tvUserSignature.text = userInfo.signature ?: "这个人很懒，什么都没留下"
        }
        
        // 设置项点击事件
        binding.layoutProfile.setOnClickListener {
            // TODO: 打开个人资料编辑页面
        }
        
        binding.layoutNotification.setOnClickListener {
            // TODO: 打开通知设置页面
        }
        
        binding.layoutPrivacy.setOnClickListener {
            // TODO: 打开隐私设置页面
        }
        
        binding.layoutAbout.setOnClickListener {
            // TODO: 打开关于页面
        }
        
        binding.btnLogout.setOnClickListener {
            logout()
        }
        
        // 开关设置
        binding.switchAutoStart.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.setAutoStartEnabled(isChecked)
        }
        
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.setNotificationEnabled(isChecked)
        }
    }
    
    private fun loadSettings() {
        binding.switchAutoStart.isChecked = preferenceManager.isAutoStartEnabled()
        binding.switchNotification.isChecked = preferenceManager.isNotificationEnabled()
    }
    
    private fun logout() {
        preferenceManager.clearUserInfo()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}