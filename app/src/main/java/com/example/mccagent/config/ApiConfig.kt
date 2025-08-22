package com.example.mccagent.config

import android.content.SharedPreferences

object ApiConfig {
    enum class Environment {
        DEV, PROD
    }

    val currentEnv: Environment
        get() = Environment.valueOf(
            prefs?.getString("env", "DEV") ?: "DEV"
        )

    // Esto se usa como fallback si no hay una base_url guardada por el usuario
    val defaultBaseUrl: String
        get() = when (currentEnv) {
            Environment.DEV -> "http://192.168.1.16:5000/api/"
            Environment.PROD -> "https://tu-backend-real.com/api/"
        }

    // Esto se inyecta desde el Application o RetrofitClient
    var prefs: SharedPreferences? = null
}
