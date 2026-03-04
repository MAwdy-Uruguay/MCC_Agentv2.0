package com.example.mccagent.models.interfaces

import com.example.mccagent.data.AuthResponse
import com.example.mccagent.data.ClientWithDevicesResponse
import com.example.mccagent.data.DeviceRegisterRequest
import com.example.mccagent.data.LoginRequest
import com.example.mccagent.data.LoginResponse
import com.example.mccagent.data.MessageListResponse
import com.example.mccagent.data.MessageStatusUpdateRequest
import com.example.mccagent.models.entities.Client
import com.example.mccagent.models.entities.Device
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("clients/me")
    suspend fun getClient(): Response<Client>

    @GET("clients/me/devices")
    suspend fun getClientWithDevices(): Response<ClientWithDevicesResponse>

    @POST("devices")
    suspend fun registerDevice(@Body request: DeviceRegisterRequest): Response<Any>

    @GET("devices")
    suspend fun getDevices(): Response<List<Device>>

    @GET("messages")
    suspend fun getPendingMessages(): Response<MessageListResponse>

    @PUT("messages/{id}/status")
    suspend fun updateMessageStatus(
        @Path("id") messageId: String,
        @Body status: MessageStatusUpdateRequest,
    ): Response<Unit>

    @GET("auth/renew")
    suspend fun renewToken(): Response<AuthResponse>
}
