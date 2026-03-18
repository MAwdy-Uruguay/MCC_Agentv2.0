package com.example.mccagent.viewmodels

import com.example.mccagent.models.entities.Device

data class ClientState(
    val isLoading: Boolean = false,
    val clientId: String = "",
    val clientName: String = "",
    val clientContact: String = "",
    val clientPhone: String = "",
    val clientAddress: String = "",
    val clientStatus: Boolean = true,
    val devices: List<Device> = emptyList(),
    val error: String? = null,
)
