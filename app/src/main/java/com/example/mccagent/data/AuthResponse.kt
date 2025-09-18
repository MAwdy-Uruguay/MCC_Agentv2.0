package com.example.mccagent.data

import com.example.mccagent.models.entities.User

data class AuthResponse(
    val token: String,
    val user: User
)
