package com.example.mccagent.models.entities

data class Message(
    val mid: String,
    val subject: String,
    val body: String,
    val recipient: String,
    val status: String,
    val type: String,
    val sentAt: String?,
    val createdAt: String?,
    val updatedAt: String?
) {
    val id: String
        get() = mid
}
