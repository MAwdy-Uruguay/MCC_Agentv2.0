package com.example.mccagent.model.interfaces

import com.example.mccagent.data.LoginRequest
import com.example.mccagent.data.LoginResponse
import retrofit2.Response

interface IAuthRepository {
    suspend fun login(request: LoginRequest): Response<LoginResponse>
}
