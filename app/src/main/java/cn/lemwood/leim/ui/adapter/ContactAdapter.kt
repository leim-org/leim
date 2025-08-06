package cn.lemwood.leim.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.lemwood.leim.data.model.Contact
import cn.lemwood.leim.databinding.ItemContactBinding

class ContactAdapter(
    private val onItemClick: (Contact) -> Unit
) : ListAdapter<Contact, ContactAdapter.ContactViewHolder>(ContactDiffCallback()) {
    
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
        
        fun bind(contact: Contact) {
            binding.apply {
                tvNickname.text = contact.remark ?: contact.nickname
                tvLeimId.text = "Leim号: ${contact.leimId}"
                tvSignature.text = contact.signature ?: "这个人很懒，什么都没留下"
                
                // 未读消息数
                if (contact.unreadCount > 0) {
                    tvUnreadCount.text = if (contact.unreadCount > 99) "99+" else contact.unreadCount.toString()
                    tvUnreadCount.visibility = android.view.View.VISIBLE
                } else {
                    tvUnreadCount.visibility = android.view.View.GONE
                }
                
                root.setOnClickListener {
                    onItemClick(contact)
                }
            }
        }
    }
    
    class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.contactId == newItem.contactId
        }
        
        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
}