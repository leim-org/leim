package cn.lemwood.leim.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.lemwood.leim.data.database.entities.Conversation
import cn.lemwood.leim.databinding.ItemConversationBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * 会话列表适配器
 */
class ConversationAdapter(
    private val onItemClick: (Conversation) -> Unit
) : ListAdapter<Conversation, ConversationAdapter.ConversationViewHolder>(ConversationDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConversationViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ConversationViewHolder(
        private val binding: ItemConversationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(conversation: Conversation) {
            binding.apply {
                textViewTitle.text = conversation.title
                textViewLastMessage.text = conversation.lastMessageContent ?: "暂无消息"
                textViewTime.text = formatTime(conversation.lastMessageTime)
                
                // 显示未读消息数
                if (conversation.unreadCount > 0) {
                    textViewUnreadCount.text = conversation.unreadCount.toString()
                    textViewUnreadCount.visibility = android.view.View.VISIBLE
                } else {
                    textViewUnreadCount.visibility = android.view.View.GONE
                }
                
                // 设置头像（这里使用默认头像）
                imageViewAvatar.setImageResource(android.R.drawable.ic_menu_gallery)
                
                root.setOnClickListener {
                    onItemClick(conversation)
                }
            }
        }
        
        private fun formatTime(timestamp: Long?): String {
            if (timestamp == null) return ""
            
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            
            return when {
                diff < 60 * 1000 -> "刚刚"
                diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
                diff < 24 * 60 * 60 * 1000 -> {
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
                }
                else -> {
                    SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date(timestamp))
                }
            }
        }
    }
    
    class ConversationDiffCallback : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.conversationId == newItem.conversationId
        }
        
        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem == newItem
        }
    }
}