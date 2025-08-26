package com.example.mccagent.config

object ApiConfig {

    enum class Environment {
        DEV, PROD
    }

    private val currentEnv = Environment.DEV

    val BASE_URL: String
        get() = when (currentEnv) {
            Environment.DEV -> "https://suy002001-DEV/mccserver/api/" //Environment.DEV -> "http://192.168.43.235:81/api/"
            Environment.PROD -> "https://suy002001-DEV/mccserver/api/"
        }
}
