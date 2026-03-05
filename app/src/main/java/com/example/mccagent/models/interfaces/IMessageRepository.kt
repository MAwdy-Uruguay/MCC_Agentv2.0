package com.example.mccagent.models.interfaces

import com.example.mccagent.models.entities.Message

interface IMessageRepository {
    suspend fun getPendingMessages(): List<Message>
    suspend fun getAllMessages(): List<Message>
    suspend fun updateMessageStatus(mid: String, status: String): Boolean
}
