package com.example.mccagent.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mccagent.repository.ClientRepositoryImpl
import com.example.mccagent.models.entities.Device
import com.example.mccagent.models.interfaces.IClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientViewModel(private val repository: IClientRepository) : ViewModel() {

    private val _clientState = MutableStateFlow(ClientState())
    val clientState: StateFlow<ClientState> = _clientState

    fun loadClientInfo() {
        viewModelScope.launch {
            _clientState.value = _clientState.value.copy(isLoading = true)

            try {
                val clientResponse = repository.getClientWithDevices()
                if (clientResponse.isSuccessful && clientResponse.body()?.ok == true) {
                    val client = clientResponse.body()!!.client
                    _clientState.value = ClientState(
                        isLoading = false,
                        clientName = client.name,
                        clientContact = client.contact_Email,
                        clientStatus = client.status,
                        devices = client.devices
                    )
                } else {
                    val errorMessage = when (clientResponse.code()) {
                        401, 403 -> "Sesión expirada. Iniciá sesión nuevamente."
                        404 -> "Cliente no encontrado."
                        else -> "Error al obtener datos del servidor"
                    }
                    _clientState.value = ClientState(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            } catch (e: Exception) {
                _clientState.value = ClientState(
                    isLoading = false,
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }
}
