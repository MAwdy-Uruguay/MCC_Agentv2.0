package com.example.mccagent.models.entities

data class User(
    val uid: String,
    val fullname: String,
    val username: String,
    val role: Role
)