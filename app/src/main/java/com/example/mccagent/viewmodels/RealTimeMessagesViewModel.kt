package com.example.mccagent.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mccagent.models.entities.Message
import com.example.mccagent.models.interfaces.IMessageRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class RealTimeUiState(
    val messages: List<Message> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val lastUpdate: String = "-"
)

class RealTimeMessagesViewModel(
    private val repository: IMessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RealTimeUiState())
    val uiState: StateFlow<RealTimeUiState> = _uiState

    private val trackedMessages = linkedMapOf<String, Message>()
    private val sentCompletedAt = mutableMapOf<String, Long>()
    private var pollingJob: Job? = null

    fun startMonitoring() {
        if (pollingJob?.isActive == true) return

        pollingJob = viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            while (isActive) {
                refresh()
                delay(3000)
            }
        }
    }

    fun stopMonitoring() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private suspend fun refresh() {
        try {
            val now = System.currentTimeMillis()
            val pending = repository.getPendingMessages()
            val allMessages = repository.getAllMessages()
            val allById = allMessages.associateBy { it.id }

            // Agrega nuevos pendientes al monitoreo.
            pending.forEach { message ->
                trackedMessages[message.id] = allById[message.id] ?: message
            }

            // Actualiza estado de todos los que ya estaban en monitoreo.
            trackedMessages.keys.toList().forEach { id ->
                val updated = allById[id]
                if (updated != null) {
                    trackedMessages[id] = updated
                }
            }

            // Marca cuando un mensaje llega a ENVIADO y lo elimina luego de 3s.
            trackedMessages.forEach { (id, message) ->
                val status = normalizeStatus(message.status)
                if (status == "ENVIADO") {
                    if (!sentCompletedAt.containsKey(id)) {
                        sentCompletedAt[id] = now
                    }
                } else {
                    sentCompletedAt.remove(id)
                }
            }

            val toRemove = sentCompletedAt
                .filterValues { now - it >= 3000L }
                .keys
                .toList()

            toRemove.forEach { id ->
                trackedMessages.remove(id)
                sentCompletedAt.remove(id)
            }

            val ordered = trackedMessages.values
                .sortedBy { it.createdAt ?: it.sentAt ?: "" }
                .takeLast(300)

            _uiState.update {
                it.copy(
                    loading = false,
                    messages = ordered,
                    error = null,
                    lastUpdate = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                        .format(java.util.Date())
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    loading = false,
                    error = e.message ?: "Error actualizando monitoreo"
                )
            }
        }
    }

    private fun normalizeStatus(status: String): String {
        return status.trim().uppercase()
    }

    override fun onCleared() {
        stopMonitoring()
        super.onCleared()
    }
}
