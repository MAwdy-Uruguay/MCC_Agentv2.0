package com.example.mccagent.config

import android.content.Context
import android.content.SharedPreferences

object ApiConfig {
    private const val PREFS_NAME = "mcc_prefs"

    const val defaultProdUrl = "https://servidor-produccion/api/"
    const val defaultPreprodUrl = "https://suy002001-dev/mccserver-dev/api/"
    const val defaultDevUrl = "https://suy002001-dev/mccserver-dev/api/"

    enum class Environment(val rawValue: String) {
        DEV("DEV"),
        PREPROD("PREPROD"),
        PROD("PROD");

        companion object {
            fun fromRaw(value: String?): Environment {
                return entries.firstOrNull { it.rawValue == value } ?: DEV
            }
        }
    }

    fun getBaseUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val prodUrl = prefs.getString("url_prod", defaultProdUrl) ?: defaultProdUrl
        val preprodUrl = prefs.getString("url_preprod", defaultPreprodUrl) ?: defaultPreprodUrl
        val devUrl = prefs.getString("url_dev", defaultDevUrl) ?: defaultDevUrl

        return when (getEnv(context)) {
            Environment.PROD -> prodUrl
            Environment.PREPROD -> preprodUrl
            Environment.DEV -> devUrl
        }
    }

    fun getEnv(context: Context): Environment {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return Environment.fromRaw(prefs.getString("env", Environment.DEV.rawValue))
    }
    var prefs: SharedPreferences? = null
}
