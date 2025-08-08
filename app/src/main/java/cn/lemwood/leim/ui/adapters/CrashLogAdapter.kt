package cn.lemwood.leim.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.lemwood.leim.databinding.ItemCrashLogBinding
import cn.lemwood.leim.utils.CrashLogManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * 崩溃日志列表适配器
 */
class CrashLogAdapter(
    private val onItemClick: (CrashLogManager.CrashLogInfo) -> Unit
) : ListAdapter<CrashLogManager.CrashLogInfo, CrashLogAdapter.ViewHolder>(DiffCallback()) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCrashLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(
        private val binding: ItemCrashLogBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(logInfo: CrashLogManager.CrashLogInfo) {
            binding.apply {
                textTime.text = dateFormat.format(Date(logInfo.timestamp))
                textSize.text = formatFileSize(logInfo.size)
                textException.text = logInfo.exceptionType
                
                root.setOnClickListener {
                    onItemClick(logInfo)
                }
            }
        }
        
        private fun formatFileSize(bytes: Long): String {
            return when {
                bytes < 1024 -> "${bytes}B"
                bytes < 1024 * 1024 -> "${bytes / 1024}KB"
                else -> "${bytes / (1024 * 1024)}MB"
            }
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<CrashLogManager.CrashLogInfo>() {
        override fun areItemsTheSame(
            oldItem: CrashLogManager.CrashLogInfo,
            newItem: CrashLogManager.CrashLogInfo
        ): Boolean {
            return oldItem.fileName == newItem.fileName
        }
        
        override fun areContentsTheSame(
            oldItem: CrashLogManager.CrashLogInfo,
            newItem: CrashLogManager.CrashLogInfo
        ): Boolean {
            return oldItem == newItem
        }
    }
}