package com.example.mccagent.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mccagent.models.entities.Message
import com.example.mccagent.models.interfaces.IMessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MessageFilter {
    PENDING,
    SENT,
    FAILED,
    ALL
}

data class MessagesUiState(
    val messages: List<Message> = emptyList(),
    val loading: Boolean = false,
    val filter: MessageFilter = MessageFilter.PENDING,
    val error: String? = null
)

class MessagesViewModel(private val repository: IMessageRepository) : ViewModel() {
    private val pageSize = 20

    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState

    private var pendingMessages: List<Message> = emptyList()
    private var allMessages: List<Message> = emptyList()
    private var filteredMessages: List<Message> = emptyList()
    private var visibleCount: Int = 0

    init {
        loadMessages(MessageFilter.PENDING)
    }

    fun loadMessages(filter: MessageFilter = _uiState.value.filter) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, filter = filter, error = null) }
            try {
                when (filter) {
                    MessageFilter.PENDING -> pendingMessages = repository.getPendingMessages()
                    MessageFilter.ALL, MessageFilter.SENT, MessageFilter.FAILED -> allMessages = repository.getAllMessages()
                }

                filteredMessages = applyFilter(filter)
                visibleCount = minOf(pageSize, filteredMessages.size)
                _uiState.update {
                    it.copy(
                        loading = false,
                        messages = filteredMessages.take(visibleCount),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        messages = emptyList(),
                        error = e.message ?: "Error al cargar mensajes"
                    )
                }
            }
        }
    }

    fun updateFilter(filter: MessageFilter) {
        loadMessages(filter)
    }

    fun loadMore() {
        if (_uiState.value.loading) return
        if (visibleCount >= filteredMessages.size) return

        visibleCount = minOf(visibleCount + pageSize, filteredMessages.size)
        _uiState.update { it.copy(messages = filteredMessages.take(visibleCount)) }
    }

    private fun applyFilter(filter: MessageFilter): List<Message> {
        return when (filter) {
            MessageFilter.PENDING -> pendingMessages
            MessageFilter.ALL -> allMessages
            MessageFilter.SENT -> allMessages.filter { it.status.equals("SENT", ignoreCase = true) }
            MessageFilter.FAILED -> allMessages.filter { it.status.equals("FAILED", ignoreCase = true) }
        }
    }
}
