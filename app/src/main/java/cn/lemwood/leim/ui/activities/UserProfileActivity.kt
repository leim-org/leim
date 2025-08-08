package cn.lemwood.leim.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import cn.lemwood.leim.R
import cn.lemwood.leim.databinding.ActivityUserProfileBinding
import cn.lemwood.leim.ui.viewmodels.UserProfileViewModel
import cn.lemwood.leim.utils.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * 用户主页Activity
 */
class UserProfileActivity : AppCompatActivity() {
    
    companion object {
        private const val EXTRA_USER_ID = "extra_user_id"
        private const val EXTRA_IS_SELF = "extra_is_self"
        
        /**
         * 启动用户主页
         */
        fun start(context: Context, userId: String, isSelf: Boolean = false) {
            val intent = Intent(context, UserProfileActivity::class.java).apply {
                putExtra(EXTRA_USER_ID, userId)
                putExtra(EXTRA_IS_SELF, isSelf)
            }
            context.startActivity(intent)
        }
    }
    
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var preferenceManager: PreferenceManager
    
    private var userId: String = ""
    private var isSelf: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferenceManager = PreferenceManager(this)
        // 初始化 ViewModel
        viewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]
        
        // 获取传入参数
        userId = intent.getStringExtra(EXTRA_USER_ID) ?: ""
        isSelf = intent.getBooleanExtra(EXTRA_IS_SELF, false)
        
        if (userId.isEmpty()) {
            Toast.makeText(this, "用户ID不能为空", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupToolbar()
        setupViews()
        setupObservers()
        
        // 加载用户信息
        viewModel.loadUserProfile(userId)
    }
    
    /**
     * 设置工具栏
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = if (isSelf) "我的主页" else "用户主页"
        }
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    /**
     * 设置视图
     */
    private fun setupViews() {
        // 根据是否为自己的主页显示不同的按钮
        if (isSelf) {
            binding.buttonSendMessage.visibility = View.GONE
            binding.buttonAddContact.visibility = View.GONE
            binding.buttonEditProfile.visibility = View.VISIBLE
        } else {
            binding.buttonEditProfile.visibility = View.GONE
        }
        
        // 设置按钮点击事件
        binding.buttonSendMessage.setOnClickListener {
            // 发送消息
            Toast.makeText(this, "发送消息功能正在开发中", Toast.LENGTH_SHORT).show()
        }
        
        binding.buttonAddContact.setOnClickListener {
            // 添加/删除联系人
            viewModel.toggleContactStatus(userId)
        }
        
        binding.buttonEditProfile.setOnClickListener {
            // 编辑个人资料
            Toast.makeText(this, "编辑个人资料功能正在开发中", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 设置观察者
     */
    private fun setupObservers() {
        viewModel.user.observe(this) { user ->
            user?.let {
                updateUserInfo(it)
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        
        viewModel.contactStatusChanged.observe(this) { isContact ->
            updateContactButton(isContact)
        }
    }
    
    /**
     * 更新用户信息显示
     */
    private fun updateUserInfo(user: cn.lemwood.leim.data.database.entities.User) {
        binding.apply {
            textViewNickname.text = user.nickname
            textViewLeimId.text = "Leim 号: ${user.leimId}"
            
            // 显示在线状态
            when (user.status) {
                "online" -> {
                    textViewStatus.text = "在线"
                    textViewStatus.setTextColor(getColor(R.color.status_online))
                    imageViewStatusIndicator.setColorFilter(getColor(R.color.status_online))
                }
                "busy" -> {
                    textViewStatus.text = "忙碌"
                    textViewStatus.setTextColor(getColor(R.color.status_busy))
                    imageViewStatusIndicator.setColorFilter(getColor(R.color.status_busy))
                }
                else -> {
                    textViewStatus.text = "离线"
                    textViewStatus.setTextColor(getColor(R.color.status_offline))
                    imageViewStatusIndicator.setColorFilter(getColor(R.color.status_offline))
                }
            }
            
            // 显示最后在线时间
            if (user.status == "offline" && user.lastSeen > 0) {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                textViewLastSeen.text = "最后在线: ${formatter.format(Date(user.lastSeen))}"
                textViewLastSeen.visibility = View.VISIBLE
            } else {
                textViewLastSeen.visibility = View.GONE
            }
            
            // 显示加入时间
            val joinFormatter = SimpleDateFormat("yyyy年MM月", Locale.getDefault())
            textViewJoinTime.text = "加入时间: ${joinFormatter.format(Date(user.createdAt))}"
            
            // 更新联系人按钮状态
            updateContactButton(user.isContact)
        }
    }
    
    /**
     * 更新联系人按钮状态
     */
    private fun updateContactButton(isContact: Boolean) {
        if (!isSelf) {
            binding.buttonAddContact.text = if (isContact) "删除联系人" else "添加联系人"
            binding.buttonAddContact.setBackgroundColor(
                getColor(if (isContact) R.color.button_danger else R.color.button_primary)
            )
        }
    }
}