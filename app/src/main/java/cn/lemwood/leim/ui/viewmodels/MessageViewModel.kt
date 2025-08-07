package cn.lemwood.leim.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.lemwood.leim.LeimApplication
import cn.lemwood.leim.data.database.entities.Message
import cn.lemwood.leim.data.repository.MessageRepository
import kotlinx.coroutines.launch

/**
 * 消息 ViewModel
 */
class MessageViewModel : ViewModel() {
    
    private val messageRepository: MessageRepository = LeimApplication.instance.messageRepository
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    /**
     * 获取会话消息
     */
    fun getMessagesByConversation(conversationId: String): LiveData<List<Message>> {
        return messageRepository.getMessagesByConversation(conversationId)
    }
    
    /**
     * 发送消息
     */
    fun sendMessage(
        conversationId: String,
        senderId: String,
        receiverId: String?,
        groupId: String?,
        content: String,
        messageType: String = "text"
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                messageRepository.sendMessage(
                    conversationId = conversationId,
                    senderId = senderId,
                    receiverId = receiverId,
                    groupId = groupId,
                    content = content,
                    messageType = messageType
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "发送消息失败"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 标记消息为已读
     */
    fun markMessagesAsRead(conversationId: String) {
        viewModelScope.launch {
            try {
                messageRepository.markMessagesAsRead(conversationId)
            } catch (e: Exception) {
                _error.value = e.message ?: "标记已读失败"
            }
        }
    }
    
    /**
     * 删除消息
     */
    fun deleteMessage(message: Message) {
        viewModelScope.launch {
            try {
                messageRepository.deleteMessage(message)
            } catch (e: Exception) {
                _error.value = e.message ?: "删除消息失败"
            }
        }
    }
    
    /**
     * 添加模拟消息（用于测试）
     */
    fun addMockMessages(conversationId: String) {
        viewModelScope.launch {
            try {
                messageRepository.addMockMessages(conversationId)
            } catch (e: Exception) {
                _error.value = e.message ?: "添加模拟消息失败"
            }
        }
    }
}