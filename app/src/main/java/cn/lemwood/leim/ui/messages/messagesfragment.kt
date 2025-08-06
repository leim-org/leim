package cn.lemwood.leim.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.lemwood.leim.databinding.FragmentMessagesBinding
import cn.lemwood.leim.ui.adapter.ConversationAdapter
import cn.lemwood.leim.viewmodel.MessagesViewModel

class MessagesFragment : Fragment() {
    
    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: MessagesViewModel
    private lateinit var conversationAdapter: ConversationAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[MessagesViewModel::class.java]
        
        setupRecyclerView()
        observeViewModel()
        setupSwipeRefresh()
    }
    
    private fun setupRecyclerView() {
        conversationAdapter = ConversationAdapter { conversation ->
            // TODO: 打开聊天界面
        }
        
        binding.recyclerViewConversations.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = conversationAdapter
        }
    }
    
    private fun observeViewModel() {
        viewModel.conversations.observe(viewLifecycleOwner) { conversations ->
            conversationAdapter.submitList(conversations)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshConversations()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}