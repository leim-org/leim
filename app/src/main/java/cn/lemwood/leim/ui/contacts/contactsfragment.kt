package cn.lemwood.leim.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.lemwood.leim.databinding.FragmentContactsBinding
import cn.lemwood.leim.ui.adapter.ContactAdapter
import cn.lemwood.leim.viewmodel.ContactsViewModel

class ContactsFragment : Fragment() {
    
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: ContactsViewModel
    private lateinit var contactAdapter: ContactAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
        
        setupRecyclerView()
        observeViewModel()
        setupSwipeRefresh()
    }
    
    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter { contact ->
            // TODO: 打开联系人详情或开始聊天
        }
        
        binding.recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contactAdapter
        }
    }
    
    private fun observeViewModel() {
        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            contactAdapter.submitList(contacts)
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
            viewModel.refreshContacts()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}