package cn.lemwood.leim.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.lemwood.leim.LeimApplication
import cn.lemwood.leim.data.model.Contact
import cn.lemwood.leim.data.repository.ContactRepository
import cn.lemwood.leim.data.websocket.WebSocketManager
import cn.lemwood.leim.utils.Constants
import kotlinx.coroutines.launch

/**
 * 联系人页面ViewModel
 * 管理联系人列表和相关操作
 */
class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val app = application as LeimApplication
    private val contactRepository = app.contactRepository
    private val webSocketManager = WebSocketManager.getInstance()
    
    // 所有联系人列表
    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts
    
    // 好友列表
    private val _friends = MutableLiveData<List<Contact>>()
    val friends: LiveData<List<Contact>> = _friends
    
    // 群组列表
    private val _groups = MutableLiveData<List<Contact>>()
    val groups: LiveData<List<Contact>> = _groups
    
    // 收藏联系人列表
    private val _favoriteContacts = MutableLiveData<List<Contact>>()
    val favoriteContacts: LiveData<List<Contact>> = _favoriteContacts
    
    // 黑名单列表
    private val _blockedContacts = MutableLiveData<List<Contact>>()
    val blockedContacts: LiveData<List<Contact>> = _blockedContacts
    
    // 搜索结果
    private val _searchResults = MutableLiveData<List<Contact>>()
    val searchResults: LiveData<List<Contact>> = _searchResults
    
    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 操作状态
    private val _isOperating = MutableLiveData<Boolean>(false)
    val isOperating: LiveData<Boolean> = _isOperating
    
    // 错误信息
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // 成功信息
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    // 搜索关键词
    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery
    
    // 当前选中的联系人
    private val _selectedContact = MutableLiveData<Contact?>()
    val selectedContact: LiveData<Contact?> = _selectedContact
    
    // 在线好友数量
    private val _onlineFriendsCount = MutableLiveData<Int>(0)
    val onlineFriendsCount: LiveData<Int> = _onlineFriendsCount
    
    // 好友申请列表
    private val _friendRequests = MutableLiveData<List<Contact>>()
    val friendRequests: LiveData<List<Contact>> = _friendRequests
    
    init {
        loadContacts()
        loadFriendRequests()
    }
    
    /**
     * 加载联系人列表
     */
    private fun loadContacts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // TODO: 从数据库加载真实联系人数据
                // val contacts = contactRepository.getAllContacts()
                
                // 模拟数据
                val mockContacts = listOf(
                    Contact(
                        id = 1,
                        leimId = "leim001",
                        nickname = "张三",
                        remark = "",
                        avatarUrl = "",
                        isOnline = true,
                        lastSeen = System.currentTimeMillis(),
                        isFriend = true,
                        isBlocked = false,
                        isFavorite = false,
                        isGroup = false,
                        groupMemberCount = null,
                        signature = "这个人很懒，什么都没留下"
                    ),
                    Contact(
                        id = 2,
                        leimId = "group001",
                        nickname = "开发群",
                        remark = "",
                        avatarUrl = "",
                        isOnline = false,
                        lastSeen = System.currentTimeMillis() - 3600000,
                        isFriend = false,
                        isBlocked = false,
                        isFavorite = true,
                        isGroup = true,
                        groupMemberCount = 15,
                        signature = "一起讨论技术问题"
                    ),
                    Contact(
                        id = 3,
                        leimId = "leim002",
                        nickname = "李四",
                        remark = "同事",
                        avatarUrl = "",
                        isOnline = false,
                        lastSeen = System.currentTimeMillis() - 7200000,
                        isFriend = true,
                        isBlocked = false,
                        isFavorite = false,
                        isGroup = false,
                        groupMemberCount = null,
                        signature = "工作使我快乐"
                    ),
                    Contact(
                        id = 4,
                        leimId = "leim003",
                        nickname = "王五",
                        remark = "",
                        avatarUrl = "",
                        isOnline = true,
                        lastSeen = System.currentTimeMillis(),
                        isFriend = true,
                        isBlocked = false,
                        isFavorite = true,
                        isGroup = false,
                        groupMemberCount = null,
                        signature = "热爱生活，热爱工作"
                    ),
                    Contact(
                        id = 5,
                        leimId = "leim004",
                        nickname = "赵六",
                        remark = "",
                        avatarUrl = "",
                        isOnline = false,
                        lastSeen = System.currentTimeMillis() - 86400000,
                        isFriend = false,
                        isBlocked = true,
                        isFavorite = false,
                        isGroup = false,
                        groupMemberCount = null,
                        signature = "已被屏蔽"
                    )
                )
                
                _contacts.value = mockContacts
                
                // 分类联系人
                categorizeContacts(mockContacts)
                
            } catch (e: Exception) {
                _errorMessage.value = "加载联系人失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 分类联系人
     */
    private fun categorizeContacts(contacts: List<Contact>) {
        val friends = contacts.filter { it.isFriend && !it.isBlocked && !it.isGroup }
        val groups = contacts.filter { it.isGroup && !it.isBlocked }
        val favorites = contacts.filter { it.isFavorite && !it.isBlocked }
        val blocked = contacts.filter { it.isBlocked }
        
        _friends.value = friends
        _groups.value = groups
        _favoriteContacts.value = favorites
        _blockedContacts.value = blocked
        
        // 计算在线好友数量
        val onlineCount = friends.count { it.isOnline }
        _onlineFriendsCount.value = onlineCount
    }
    
    /**
     * 刷新联系人列表
     */
    fun refreshContacts() {
        loadContacts()
    }
    
    /**
     * 搜索联系人
     */
    fun searchContacts(query: String) {
        viewModelScope.launch {
            try {
                _searchQuery.value = query
                
                val allContacts = _contacts.value ?: emptyList()
                val results = if (query.isBlank()) {
                    emptyList()
                } else {
                    allContacts.filter { contact ->
                        contact.nickname.contains(query, ignoreCase = true) ||
                        contact.leimId.contains(query, ignoreCase = true) ||
                        contact.remark.contains(query, ignoreCase = true) ||
                        contact.signature.contains(query, ignoreCase = true)
                    }
                }
                
                _searchResults.value = results
                
            } catch (e: Exception) {
                _errorMessage.value = "搜索失败: ${e.message}"
            }
        }
    }
    
    /**
     * 通过Leim ID搜索用户
     */
    fun searchUserByLeimId(leimId: String) {
        viewModelScope.launch {
            try {
                _isOperating.value = true
                
                // TODO: 调用API搜索用户
                // val user = contactRepository.searchUserByLeimId(leimId)
                
                // 模拟搜索结果
                if (leimId.isNotBlank()) {
                    val mockUser = Contact(
                        id = System.currentTimeMillis().toInt(),
                        leimId = leimId,
                        nickname = "用户$leimId",
                        remark = "",
                        avatarUrl = "",
                        isOnline = false,
                        lastSeen = System.currentTimeMillis(),
                        isFriend = false,
                        isBlocked = false,
                        isFavorite = false,
                        isGroup = false,
                        groupMemberCount = null,
                        signature = "新用户"
                    )
                    _selectedContact.value = mockUser
                } else {
                    _errorMessage.value = "请输入有效的Leim ID"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "搜索用户失败: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
    
    /**
     * 添加好友
     */
    fun addFriend(contact: Contact, message: String = "") {
        viewModelScope.launch {
            try {
                _isOperating.value = true
                
                // TODO: 发送好友申请
                // contactRepository.sendFriendRequest(contact.id, message)
                
                // 模拟添加好友
                _successMessage.value = "好友申请已发送"
                
                // 通过WebSocket发送好友申请
                if (webSocketManager.isConnected()) {
                    // TODO: 发送WebSocket消息
                    // webSocketManager.sendFriendRequest(contact.id, message)
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "发送好友申请失败: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
    
    /**
     * 删除好友
     */
    fun deleteFriend(contactId: Int) {
        viewModelScope.launch {
            try {
                _isOperating.value = true
                
                // TODO: 删除好友
                // contactRepository.deleteFriend(contactId)
                
                // 从本地列表中移除
                val contacts = _contacts.value?.toMutableList() ?: return@launch
                contacts.removeAll { it.id == contactId }
                _contacts.value = contacts
                categorizeContacts(contacts)
                
                _successMessage.value = "已删除好友"
                
            } catch (e: Exception) {
                _errorMessage.value = "删除好友失败: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
    
    /**
     * 屏蔽/取消屏蔽联系人
     */
    fun toggleBlockContact(contactId: Int) {
        viewModelScope.launch {
            try {
                _isOperating.value = true
                
                val contacts = _contacts.value?.toMutableList() ?: return@launch
                val contactIndex = contacts.indexOfFirst { it.id == contactId }
                if (contactIndex != -1) {
                    val contact = contacts[contactIndex]
                    val newBlockStatus = !contact.isBlocked
                    
                    contacts[contactIndex] = contact.copy(isBlocked = newBlockStatus)
                    _contacts.value = contacts
                    categorizeContacts(contacts)
                    
                    // TODO: 更新数据库
                    // contactRepository.updateBlockStatus(contactId, newBlockStatus)
                    
                    _successMessage.value = if (newBlockStatus) "已屏蔽该联系人" else "已取消屏蔽"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "操作失败: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
    
    /**
     * 收藏/取消收藏联系人
     */
    fun toggleFavoriteContact(contactId: Int) {
        viewModelScope.launch {
            try {
                _isOperating.value = true
                
                val contacts = _contacts.value?.toMutableList() ?: return@launch
                val contactIndex = contacts.indexOfFirst { it.id == contactId }
                if (contactIndex != -1) {
                    val contact = contacts[contactIndex]
                    val newFavoriteStatus = !contact.isFavorite
                    
                    contacts[contactIndex] = contact.copy(isFavorite = newFavoriteStatus)
                    _contacts.value = contacts
                    categorizeContacts(contacts)
                    
                    // TODO: 更新数据库
                    // contactRepository.updateFavoriteStatus(contactId, newFavoriteStatus)
                    
                    _successMessage.value = if (newFavoriteStatus) "已添加到收藏" else "已取消收藏"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "操作失败: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
    
    /**
     * 更新联系人备注
     */
    fun updateContactRemark(contactId: Int, remark: String) {
        viewModelScope.launch {
            try {
                _isOperating.value = true
                
                val contacts = _contacts.value?.toMutableList() ?: return@launch
                val contactIndex = contacts.indexOfFirst { it.id == contactId }
                if (contactIndex != -1) {
                    contacts[contactIndex] = contacts[contactIndex].copy(remark = remark)
                    _contacts.value = contacts
                    categorizeContacts(contacts)
                    
                    // TODO: 更新数据库
                    // contactRepository.updateContactRemark(contactId, remark)
                    
                    _successMessage.value = "备注已更新"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "更新备注失败: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
    
    /**
     * 创建群组
     */
    fun createGroup(groupName: String, memberIds: List<Int>) {
        viewModelScope.launch {
            try {
                _isOperating.value = true
                
                // TODO: 创建群组
                // val group = contactRepository.createGroup(groupName, memberIds)
                
                // 模拟创建群组
                val newGroup = Contact(
                    id = System.currentTimeMillis().toInt(),
                    leimId = "group${System.currentTimeMillis()}",
                    nickname = groupName,
                    remark = "",
                    avatarUrl = "",
                    isOnline = false,
                    lastSeen = System.currentTimeMillis(),
                    isFriend = false,
                    isBlocked = false,
                    isFavorite = false,
                    isGroup = true,
                    groupMemberCount = memberIds.size + 1, // 包括创建者
                    signature = "新建群组"
                )
                
                val contacts = _contacts.value?.toMutableList() ?: mutableListOf()
                contacts.add(newGroup)
                _contacts.value = contacts
                categorizeContacts(contacts)
                
                _successMessage.value = "群组创建成功"
                
            } catch (e: Exception) {
                _errorMessage.value = "创建群组失败: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
    
    /**
     * 加入群组
     */
    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            try {
                _isOperating.value = true
                
                // TODO: 加入群组
                // contactRepository.joinGroup(groupId)
                
                _successMessage.value = "已申请加入群组"
                
            } catch (e: Exception) {
                _errorMessage.value = "加入群组失败: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
    
    /**
     * 退出群组
     */
    fun leaveGroup(groupId: Int) {
        viewModelScope.launch {
            try {
                _isOperating.value = true
                
                // TODO: 退出群组
                // contactRepository.leaveGroup(groupId)
                
                // 从本地列表中移除
                val contacts = _contacts.value?.toMutableList() ?: return@launch
                contacts.removeAll { it.id == groupId }
                _contacts.value = contacts
                categorizeContacts(contacts)
                
                _successMessage.value = "已退出群组"
                
            } catch (e: Exception) {
                _errorMessage.value = "退出群组失败: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
    
    /**
     * 加载好友申请列表
     */
    private fun loadFriendRequests() {
        viewModelScope.launch {
            try {
                // TODO: 从数据库加载好友申请
                // val requests = contactRepository.getFriendRequests()
                
                // 模拟好友申请数据
                val mockRequests = listOf(
                    Contact(
                        id = 100,
                        leimId = "leim100",
                        nickname = "新朋友",
                        remark = "",
                        avatarUrl = "",
                        isOnline = false,
                        lastSeen = System.currentTimeMillis(),
                        isFriend = false,
                        isBlocked = false,
                        isFavorite = false,
                        isGroup = false,
                        groupMemberCount = null,
                        signature = "希望成为朋友"
                    )
                )
                
                _friendRequests.value = mockRequests
                
            } catch (e: Exception) {
                _errorMessage.value = "加载好友申请失败: ${e.message}"
            }
        }
    }
    
    /**
     * 处理好友申请
     */
    fun handleFriendRequest(requestId: Int, accept: Boolean) {
        viewModelScope.launch {
            try {
                _isOperating.value = true
                
                // TODO: 处理好友申请
                // contactRepository.handleFriendRequest(requestId, accept)
                
                // 从申请列表中移除
                val requests = _friendRequests.value?.toMutableList() ?: return@launch
                val request = requests.find { it.id == requestId }
                requests.removeAll { it.id == requestId }
                _friendRequests.value = requests
                
                if (accept && request != null) {
                    // 添加到好友列表
                    val contacts = _contacts.value?.toMutableList() ?: mutableListOf()
                    contacts.add(request.copy(isFriend = true))
                    _contacts.value = contacts
                    categorizeContacts(contacts)
                    
                    _successMessage.value = "已添加为好友"
                } else {
                    _successMessage.value = "已拒绝好友申请"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "处理好友申请失败: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
    
    /**
     * 更新联系人在线状态
     */
    fun updateContactOnlineStatus(contactId: Int, isOnline: Boolean) {
        viewModelScope.launch {
            try {
                val contacts = _contacts.value?.toMutableList() ?: return@launch
                val contactIndex = contacts.indexOfFirst { it.id == contactId }
                if (contactIndex != -1) {
                    contacts[contactIndex] = contacts[contactIndex].copy(
                        isOnline = isOnline,
                        lastSeen = if (!isOnline) System.currentTimeMillis() else contacts[contactIndex].lastSeen
                    )
                    _contacts.value = contacts
                    categorizeContacts(contacts)
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "更新状态失败: ${e.message}"
            }
        }
    }
    
    /**
     * 获取联系人详情
     */
    fun getContactDetail(contactId: Int) {
        viewModelScope.launch {
            try {
                val contact = _contacts.value?.find { it.id == contactId }
                _selectedContact.value = contact
                
            } catch (e: Exception) {
                _errorMessage.value = "获取联系人详情失败: ${e.message}"
            }
        }
    }
    
    /**
     * 清除选中的联系人
     */
    fun clearSelectedContact() {
        _selectedContact.value = null
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * 清除成功信息
     */
    fun clearSuccess() {
        _successMessage.value = null
    }
}