package cn.lemwood.leim.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.lemwood.leim.databinding.FragmentContactBinding
import cn.lemwood.leim.ui.activities.UserProfileActivity
import cn.lemwood.leim.ui.adapters.ContactAdapter
import cn.lemwood.leim.ui.viewmodels.ContactViewModel

/**
 * 联系人页面 Fragment
 */
class ContactFragment : Fragment() {
    
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var contactViewModel: ContactViewModel
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
        
        contactViewModel = ViewModelProvider(this)[ContactViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        setupFab()
        
        // 添加模拟联系人
        contactViewModel.addMockContacts()
    }
    
    /**
     * 设置 RecyclerView
     */
    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter { user ->
            // 点击联系人，打开用户主页
            UserProfileActivity.start(requireContext(), user.leimId, false)
        }
        
        binding.recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contactAdapter
        }
    }
    
    /**
     * 设置观察者
     */
    private fun setupObservers() {
        contactViewModel.getAllContacts().observe(viewLifecycleOwner) { contacts ->
            if (contacts.isNotEmpty()) {
                binding.recyclerViewContacts.visibility = View.VISIBLE
                binding.textViewEmpty.visibility = View.GONE
                contactAdapter.submitList(contacts)
            } else {
                binding.recyclerViewContacts.visibility = View.GONE
                binding.textViewEmpty.visibility = View.VISIBLE
            }
        }
        
        contactViewModel.error.observe(viewLifecycleOwner) { error ->
            // 显示错误信息
            // TODO: 实现错误提示
        }
    }
    
    /**
     * 设置悬浮按钮
     */
    private fun setupFab() {
        binding.fabAddContact.setOnClickListener {
            // 添加联系人功能
            // TODO: 实现添加联系人对话框
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}