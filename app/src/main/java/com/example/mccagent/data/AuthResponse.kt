package com.example.mccagent.data

import com.example.mccagent.models.entities.UserData

data class AuthResponse(
    val token: String,
    val user: UserData
)
