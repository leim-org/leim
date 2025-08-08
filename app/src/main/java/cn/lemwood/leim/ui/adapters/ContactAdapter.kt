package cn.lemwood.leim.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.lemwood.leim.data.database.entities.User
import cn.lemwood.leim.databinding.ItemContactBinding

/**
 * 联系人列表适配器
 */
class ContactAdapter(
    private val onItemClick: (User) -> Unit
) : ListAdapter<User, ContactAdapter.ContactViewHolder>(ContactDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ContactViewHolder(
        private val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(user: User) {
            binding.apply {
                textViewNickname.text = user.nickname
                textViewLeimId.text = "Leim 号: ${user.leimId}"
                
                // 显示在线状态
                when (user.status) {
                    "online" -> {
                        textViewStatus.text = "在线"
                        textViewStatus.setTextColor(0xFF4CAF50.toInt()) // 绿色
                    }
                    "offline" -> {
                        textViewStatus.text = "离线"
                        textViewStatus.setTextColor(0xFF757575.toInt()) // 灰色
                    }
                    "away", "busy" -> {
                        textViewStatus.text = if (user.status == "away") "离开" else "忙碌"
                        textViewStatus.setTextColor(0xFFFF9800.toInt()) // 橙色
                    }
                    else -> {
                        textViewStatus.text = "未知"
                        textViewStatus.setTextColor(0xFF757575.toInt()) // 灰色
                    }
                }
                
                // 设置头像（这里使用默认头像）
                imageViewAvatar.setImageResource(android.R.drawable.ic_menu_gallery)
                
                root.setOnClickListener {
                    onItemClick(user)
                }
            }
        }
    }
    
    class ContactDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.leimId == newItem.leimId
        }
        
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}