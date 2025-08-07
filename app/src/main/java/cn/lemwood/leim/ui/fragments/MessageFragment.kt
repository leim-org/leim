package cn.lemwood.leim.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.lemwood.leim.databinding.FragmentMessageBinding
import cn.lemwood.leim.ui.adapters.ConversationAdapter
import cn.lemwood.leim.ui.viewmodels.MessageViewModel

/**
 * 消息页面 Fragment
 */
class MessageFragment : Fragment() {
    
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var conversationAdapter: ConversationAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        
        // 添加模拟数据
        addMockData()
    }
    
    /**
     * 设置 RecyclerView
     */
    private fun setupRecyclerView() {
        conversationAdapter = ConversationAdapter { conversation ->
            // 点击会话项，跳转到聊天界面
            // TODO: 实现聊天界面
        }
        
        binding.recyclerViewConversations.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = conversationAdapter
        }
    }
    
    /**
     * 设置观察者
     */
    private fun setupObservers() {
        // 这里应该观察会话列表的变化
        // 由于当前只是框架，暂时显示空状态
        showEmptyState()
    }
    
    /**
     * 显示空状态
     */
    private fun showEmptyState() {
        binding.textViewEmpty.visibility = View.VISIBLE
        binding.recyclerViewConversations.visibility = View.GONE
    }
    
    /**
     * 添加模拟数据
     */
    private fun addMockData() {
        // 这里可以添加一些模拟的会话数据用于展示
        binding.textViewEmpty.text = "暂无会话\n\n后端服务器正在施工中\n请稍后再试"
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}