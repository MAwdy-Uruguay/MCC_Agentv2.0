package com.example.mccagent.repository

import android.content.Context
import android.util.Log
import com.example.mccagent.data.MessageStatusUpdateRequest
import com.example.mccagent.models.interfaces.IMessageRepository
import com.example.mccagent.models.entities.Message
import com.example.mccagent.models.interfaces.IApiService
import com.example.mccagent.network.RetrofitClient
import com.example.mccagent.network.RetrofitClient.getApiWithValidToken
import retrofit2.Response

class MessageRepositoryImpl(private val context: Context) : IMessageRepository {

    private suspend fun getApi(): IApiService {
        return RetrofitClient.getApiWithValidToken(context)
    }

    override suspend fun getPendingMessages(): List<Message> {
        return try {
            val response = getApi().getPendingMessages()
            if (response.isSuccessful) {
                response.body()?.messages ?: emptyList()
            } else {
                Log.e("MessageRepo", "❌ Error al obtener mensajes: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("MessageRepo", "💥 Excepción al obtener mensajes: ${e.message}")
            emptyList()
        }
    }

    override suspend fun updateMessageStatus(mid: String, status: String): Boolean {
        return try {
            val response = getApi().updateMessageStatus(mid, MessageStatusUpdateRequest(status))
            if (!response.isSuccessful) {
                val detalle = response.errorBody()?.string()?.take(500)
                Log.e(
                    "MessageRepo",
                    "❌ Error al actualizar estado de $mid a $status: ${response.code()} - ${detalle ?: "sin detalle"}"
                )
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("MessageRepo", "💥 Excepción al actualizar estado: ${e.message}")
            false
        }
    }
}

