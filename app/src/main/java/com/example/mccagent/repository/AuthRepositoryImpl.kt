package com.example.mccagent.repository

import com.example.mccagent.data.LoginRequest
import com.example.mccagent.data.LoginResponse
import com.example.mccagent.model.interfaces.IAuthRepository
import com.example.mccagent.models.interfaces.IApiService
import retrofit2.Response

class AuthRepositoryImpl(
    private val apiService: IApiService,
) : IAuthRepository {

    override suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }
}
