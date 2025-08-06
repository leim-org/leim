package cn.lemwood.leim.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.lemwood.leim.LeimApplication
import cn.lemwood.leim.data.model.Conversation
import cn.lemwood.leim.data.model.Message
import cn.lemwood.leim.data.repository.MessageRepository
import cn.lemwood.leim.data.websocket.WebSocketManager
import cn.lemwood.leim.utils.Constants
import kotlinx.coroutines.launch
import java.util.*

/**
 * 消息页面ViewModel
 * 管理会话列表和消息相关操作
 */
class MessagesViewModel(application: Application) : AndroidViewModel(application) {
    
    private val app = application as LeimApplication
    private val messageRepository = app.messageRepository
    private val webSocketManager = WebSocketManager.getInstance()
    
    // 会话列表
    private val _conversations = MutableLiveData<List<Conversation>>()
    val conversations: LiveData<List<Conversation>> = _conversations
    
    // 当前会话的消息列表
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages
    
    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 发送状态
    private val _isSending = MutableLiveData<Boolean>(false)
    val isSending: LiveData<Boolean> = _isSending
    
    // 错误信息
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // 搜索关键词
    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery
    
    // 过滤后的会话列表
    private val _filteredConversations = MutableLiveData<List<Conversation>>()
    val filteredConversations: LiveData<List<Conversation>> = _filteredConversations
    
    // 当前会话ID
    private val _currentConversationId = MutableLiveData<Int?>()
    val currentConversationId: LiveData<Int?> = _currentConversationId
    
    // 总未读消息数
    private val _totalUnreadCount = MutableLiveData<Int>(0)
    val totalUnreadCount: LiveData<Int> = _totalUnreadCount
    
    init {
        loadConversations()
    }
    
    /**
     * 加载会话列表
     */
    private fun loadConversations() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // TODO: 从数据库加载真实会话数据
                // val conversations = messageRepository.getAllConversations()
                
                // 模拟数据
                val mockConversations = listOf(
                    Conversation(
                        id = 1,
                        title = "张三",
                        lastMessage = "你好，最近怎么样？",
                        lastMessageTime = Date(System.currentTimeMillis() - 60000),
                        unreadCount = 2,
                        avatarUrl = "",
                        isGroup = false,
                        isPinned = false,
                        isMuted = false,
                        isDraft = false,
                        draftContent = ""
                    ),
                    Conversation(
                        id = 2,
                        title = "开发群",
                        lastMessage = "今天的任务完成了吗？",
                        lastMessageTime = Date(System.currentTimeMillis() - 3600000),
                        unreadCount = 5,
                        avatarUrl = "",
                        isGroup = true,
                        isPinned = true,
                        isMuted = false,
                        isDraft = false,
                        draftContent = ""
                    ),
                    Conversation(
                        id = 3,
                        title = "李四",
                        lastMessage = "明天见面聊",
                        lastMessageTime = Date(System.currentTimeMillis() - 7200000),
                        unreadCount = 0,
                        avatarUrl = "",
                        isGroup = false,
                        isPinned = false,
                        isMuted = true,
                        isDraft = true,
                        draftContent = "好的，明天"
                    )
                )
                
                _conversations.value = mockConversations
                _filteredConversations.value = mockConversations
                
                // 计算总未读数
                val totalUnread = mockConversations.sumOf { it.unreadCount }
                _totalUnreadCount.value = totalUnread
                
            } catch (e: Exception) {
                _errorMessage.value = "加载会话列表失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 刷新会话列表
     */
    fun refreshConversations() {
        loadConversations()
    }
    
    /**
     * 搜索会话
     */
    fun searchConversations(query: String) {
        viewModelScope.launch {
            try {
                _searchQuery.value = query
                
                val allConversations = _conversations.value ?: emptyList()
                val filtered = if (query.isBlank()) {
                    allConversations
                } else {
                    allConversations.filter { conversation ->
                        conversation.title.contains(query, ignoreCase = true) ||
                        conversation.lastMessage.contains(query, ignoreCase = true)
                    }
                }
                
                _filteredConversations.value = filtered
                
            } catch (e: Exception) {
                _errorMessage.value = "搜索失败: ${e.message}"
            }
        }
    }
    
    /**
     * 加载指定会话的消息
     */
    fun loadMessages(conversationId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _currentConversationId.value = conversationId
                
                // TODO: 从数据库加载真实消息数据
                // val messages = messageRepository.getMessagesByConversation(conversationId)
                
                // 模拟消息数据
                val mockMessages = listOf(
                    Message(
                        id = 1,
                        conversationId = conversationId,
                        senderId = 1,
                        senderName = "张三",
                        content = "你好，最近怎么样？",
                        type = Constants.MESSAGE_TYPE_TEXT,
                        timestamp = Date(System.currentTimeMillis() - 120000),
                        isRead = false,
                        isSent = true,
                        isDelivered = true,
                        replyToMessageId = null,
                        fileUrl = null,
                        fileName = null,
                        fileSize = null,
                        thumbnailUrl = null
                    ),
                    Message(
                        id = 2,
                        conversationId = conversationId,
                        senderId = 2,
                        senderName = "我",
                        content = "还不错，你呢？",
                        type = Constants.MESSAGE_TYPE_TEXT,
                        timestamp = Date(System.currentTimeMillis() - 60000),
                        isRead = true,
                        isSent = true,
                        isDelivered = true,
                        replyToMessageId = 1,
                        fileUrl = null,
                        fileName = null,
                        fileSize = null,
                        thumbnailUrl = null
                    )
                )
                
                _messages.value = mockMessages
                
                // 标记消息为已读
                markMessagesAsRead(conversationId)
                
            } catch (e: Exception) {
                _errorMessage.value = "加载消息失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 发送文本消息
     */
    fun sendTextMessage(conversationId: Int, content: String, replyToMessageId: Int? = null) {
        viewModelScope.launch {
            try {
                _isSending.value = true
                
                val message = Message(
                    id = System.currentTimeMillis().toInt(),
                    conversationId = conversationId,
                    senderId = 1, // TODO: 获取当前用户ID
                    senderName = "我",
                    content = content,
                    type = Constants.MESSAGE_TYPE_TEXT,
                    timestamp = Date(),
                    isRead = false,
                    isSent = false,
                    isDelivered = false,
                    replyToMessageId = replyToMessageId,
                    fileUrl = null,
                    fileName = null,
                    fileSize = null,
                    thumbnailUrl = null
                )
                
                // 添加到本地消息列表
                val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
                currentMessages.add(message)
                _messages.value = currentMessages
                
                // TODO: 保存到数据库
                // messageRepository.insertMessage(message)
                
                // 通过WebSocket发送消息
                if (webSocketManager.isConnected()) {
                    // TODO: 发送WebSocket消息
                    // webSocketManager.sendMessage(message)
                }
                
                // 更新会话的最后消息
                updateLastMessage(conversationId, content)
                
            } catch (e: Exception) {
                _errorMessage.value = "发送消息失败: ${e.message}"
            } finally {
                _isSending.value = false
            }
        }
    }
    
    /**
     * 发送图片消息
     */
    fun sendImageMessage(conversationId: Int, imageUrl: String, thumbnailUrl: String? = null) {
        viewModelScope.launch {
            try {
                _isSending.value = true
                
                val message = Message(
                    id = System.currentTimeMillis().toInt(),
                    conversationId = conversationId,
                    senderId = 1,
                    senderName = "我",
                    content = "[图片]",
                    type = Constants.MESSAGE_TYPE_IMAGE,
                    timestamp = Date(),
                    isRead = false,
                    isSent = false,
                    isDelivered = false,
                    replyToMessageId = null,
                    fileUrl = imageUrl,
                    fileName = "image.jpg",
                    fileSize = null,
                    thumbnailUrl = thumbnailUrl
                )
                
                val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
                currentMessages.add(message)
                _messages.value = currentMessages
                
                updateLastMessage(conversationId, "[图片]")
                
            } catch (e: Exception) {
                _errorMessage.value = "发送图片失败: ${e.message}"
            } finally {
                _isSending.value = false
            }
        }
    }
    
    /**
     * 发送文件消息
     */
    fun sendFileMessage(conversationId: Int, fileUrl: String, fileName: String, fileSize: Long) {
        viewModelScope.launch {
            try {
                _isSending.value = true
                
                val message = Message(
                    id = System.currentTimeMillis().toInt(),
                    conversationId = conversationId,
                    senderId = 1,
                    senderName = "我",
                    content = "[文件] $fileName",
                    type = Constants.MESSAGE_TYPE_FILE,
                    timestamp = Date(),
                    isRead = false,
                    isSent = false,
                    isDelivered = false,
                    replyToMessageId = null,
                    fileUrl = fileUrl,
                    fileName = fileName,
                    fileSize = fileSize,
                    thumbnailUrl = null
                )
                
                val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
                currentMessages.add(message)
                _messages.value = currentMessages
                
                updateLastMessage(conversationId, "[文件] $fileName")
                
            } catch (e: Exception) {
                _errorMessage.value = "发送文件失败: ${e.message}"
            } finally {
                _isSending.value = false
            }
        }
    }
    
    /**
     * 标记消息为已读
     */
    fun markMessagesAsRead(conversationId: Int) {
        viewModelScope.launch {
            try {
                // TODO: 更新数据库中的消息状态
                // messageRepository.markMessagesAsRead(conversationId)
                
                // 更新会话的未读数
                val conversations = _conversations.value?.toMutableList() ?: return@launch
                val conversationIndex = conversations.indexOfFirst { it.id == conversationId }
                if (conversationIndex != -1) {
                    conversations[conversationIndex] = conversations[conversationIndex].copy(unreadCount = 0)
                    _conversations.value = conversations
                    _filteredConversations.value = conversations
                    
                    // 重新计算总未读数
                    val totalUnread = conversations.sumOf { it.unreadCount }
                    _totalUnreadCount.value = totalUnread
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "标记已读失败: ${e.message}"
            }
        }
    }
    
    /**
     * 置顶/取消置顶会话
     */
    fun togglePinConversation(conversationId: Int) {
        viewModelScope.launch {
            try {
                val conversations = _conversations.value?.toMutableList() ?: return@launch
                val conversationIndex = conversations.indexOfFirst { it.id == conversationId }
                if (conversationIndex != -1) {
                    val conversation = conversations[conversationIndex]
                    conversations[conversationIndex] = conversation.copy(isPinned = !conversation.isPinned)
                    
                    // 重新排序：置顶的会话排在前面
                    conversations.sortWith(compareByDescending<Conversation> { it.isPinned }.thenByDescending { it.lastMessageTime })
                    
                    _conversations.value = conversations
                    _filteredConversations.value = conversations
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "操作失败: ${e.message}"
            }
        }
    }
    
    /**
     * 静音/取消静音会话
     */
    fun toggleMuteConversation(conversationId: Int) {
        viewModelScope.launch {
            try {
                val conversations = _conversations.value?.toMutableList() ?: return@launch
                val conversationIndex = conversations.indexOfFirst { it.id == conversationId }
                if (conversationIndex != -1) {
                    val conversation = conversations[conversationIndex]
                    conversations[conversationIndex] = conversation.copy(isMuted = !conversation.isMuted)
                    _conversations.value = conversations
                    _filteredConversations.value = conversations
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "操作失败: ${e.message}"
            }
        }
    }
    
    /**
     * 删除会话
     */
    fun deleteConversation(conversationId: Int) {
        viewModelScope.launch {
            try {
                // TODO: 从数据库删除会话和相关消息
                // messageRepository.deleteConversation(conversationId)
                
                val conversations = _conversations.value?.toMutableList() ?: return@launch
                conversations.removeAll { it.id == conversationId }
                _conversations.value = conversations
                _filteredConversations.value = conversations
                
                // 重新计算总未读数
                val totalUnread = conversations.sumOf { it.unreadCount }
                _totalUnreadCount.value = totalUnread
                
            } catch (e: Exception) {
                _errorMessage.value = "删除会话失败: ${e.message}"
            }
        }
    }
    
    /**
     * 更新会话的最后消息
     */
    private fun updateLastMessage(conversationId: Int, content: String) {
        val conversations = _conversations.value?.toMutableList() ?: return
        val conversationIndex = conversations.indexOfFirst { it.id == conversationId }
        if (conversationIndex != -1) {
            conversations[conversationIndex] = conversations[conversationIndex].copy(
                lastMessage = content,
                lastMessageTime = Date()
            )
            
            // 重新排序
            conversations.sortWith(compareByDescending<Conversation> { it.isPinned }.thenByDescending { it.lastMessageTime })
            
            _conversations.value = conversations
            _filteredConversations.value = conversations
        }
    }
    
    /**
     * 保存草稿
     */
    fun saveDraft(conversationId: Int, content: String) {
        viewModelScope.launch {
            try {
                val conversations = _conversations.value?.toMutableList() ?: return@launch
                val conversationIndex = conversations.indexOfFirst { it.id == conversationId }
                if (conversationIndex != -1) {
                    conversations[conversationIndex] = conversations[conversationIndex].copy(
                        isDraft = content.isNotBlank(),
                        draftContent = content
                    )
                    _conversations.value = conversations
                    _filteredConversations.value = conversations
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "保存草稿失败: ${e.message}"
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }
}