package com.example.mccagent.repository

import android.content.Context
import com.example.mccagent.data.ClientWithDevicesResponse
import com.example.mccagent.models.entities.Client
import com.example.mccagent.models.entities.Device
import com.example.mccagent.models.interfaces.IClientRepository
import com.example.mccagent.models.interfaces.IApiService
import com.example.mccagent.network.RetrofitClient.getApiWithValidToken
import retrofit2.Response

class ClientRepositoryImpl(private val context: Context) : IClientRepository {

    private suspend fun getApi(): IApiService {
        return getApiWithValidToken(context)
    }

    override suspend fun getClient(): Response<Client> {
        return getApi().getClient()
    }

    override suspend fun getClientWithDevices(): Response<ClientWithDevicesResponse> {
        return getApi().getClientWithDevices()
    }

    override suspend fun getDevices(): Response<List<Device>> {
        return getApi().getDevices()
    }
}



