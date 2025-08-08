package cn.lemwood.leim.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.lemwood.leim.databinding.FragmentContactBinding
import cn.lemwood.leim.ui.adapters.ContactAdapter
import cn.lemwood.leim.ui.viewmodels.ContactViewModel
import kotlinx.coroutines.launch

/**
 * 联系人页面
 */
class ContactFragment : Fragment() {
    
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!
    
    private val contactViewModel: ContactViewModel by viewModels()
    private lateinit var contactAdapter: ContactAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        // 在协程中添加模拟联系人
        lifecycleScope.launch {
            contactViewModel.addMockContacts()
        }
    }
    
    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter { user ->
            // 处理联系人点击事件
            // TODO: 导航到聊天页面或用户详情页面
        }
        
        binding.recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }
    }
    
    private fun setupObservers() {
        // 观察联系人列表
        contactViewModel.getAllContacts().observe(viewLifecycleOwner) { contacts ->
            contactAdapter.submitList(contacts)
            
            // 显示/隐藏空状态
            if (contacts.isEmpty()) {
                binding.recyclerViewContacts.visibility = View.GONE
                binding.textViewEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerViewContacts.visibility = View.VISIBLE
                binding.textViewEmpty.visibility = View.GONE
            }
        }
        
        // 观察加载状态
        contactViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: 显示/隐藏加载指示器
        }
        
        // 观察错误信息
        contactViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                // TODO: 显示错误信息
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddContact.setOnClickListener {
            // TODO: 打开添加联系人页面
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}