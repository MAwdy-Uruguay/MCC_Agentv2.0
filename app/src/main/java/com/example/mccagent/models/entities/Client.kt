package com.example.mccagent.models.entities


data class Client(
    val cid: String,
    val name: String,
    val contact_Email: String,
    val phone: String,
    val address: String,
    val active: Boolean,
    val devices: List<Device>,
    val status: Boolean
)
