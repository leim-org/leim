package cn.lemwood.leim.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.lemwood.leim.data.model.Conversation
import cn.lemwood.leim.databinding.ItemConversationBinding
import java.text.SimpleDateFormat
import java.util.*

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
                tvTitle.text = conversation.title
                tvLastMessage.text = conversation.lastMessageContent ?: "暂无消息"
                tvTime.text = formatTime(conversation.lastMessageTime)
                
                // 未读消息数
                if (conversation.unreadCount > 0) {
                    tvUnreadCount.text = if (conversation.unreadCount > 99) "99+" else conversation.unreadCount.toString()
                    tvUnreadCount.visibility = android.view.View.VISIBLE
                } else {
                    tvUnreadCount.visibility = android.view.View.GONE
                }
                
                // 置顶标识
                ivPinned.visibility = if (conversation.isPinned) android.view.View.VISIBLE else android.view.View.GONE
                
                // 免打扰标识
                ivMuted.visibility = if (conversation.isMuted) android.view.View.VISIBLE else android.view.View.GONE
                
                root.setOnClickListener {
                    onItemClick(conversation)
                }
            }
        }
        
        private fun formatTime(timestamp: Long): String {
            if (timestamp == 0L) return ""
            
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            val calendar = Calendar.getInstance()
            val messageCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
            
            return when {
                diff < 60 * 1000 -> "刚刚"
                diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
                calendar.get(Calendar.DAY_OF_YEAR) == messageCalendar.get(Calendar.DAY_OF_YEAR) -> {
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
                }
                diff < 7 * 24 * 60 * 60 * 1000 -> {
                    SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date(timestamp))
                }
                else -> {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
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