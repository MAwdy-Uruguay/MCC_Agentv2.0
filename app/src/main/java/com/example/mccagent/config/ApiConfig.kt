package com.example.mccagent.config

import android.content.Context
import android.content.SharedPreferences

object ApiConfig {
    private const val PREFS_NAME = "mcc_prefs"

    const val defaultProdUrl = "https://servidor-produccion/api/"
    const val defaultPreprodUrl = "https://suy002001-dev/mccserver-dev/api/"
    const val defaultDevUrl = "https://suy002001-dev/mccserver-dev/api/"

    fun getBaseUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val prodUrl = prefs.getString("url_prod", defaultProdUrl) ?: defaultProdUrl
        val preprodUrl = prefs.getString("url_preprod", defaultPreprodUrl) ?: defaultPreprodUrl
        val devUrl = prefs.getString("url_dev", defaultDevUrl) ?: defaultDevUrl

        return when (prefs.getString("env", "DEV")) {
            "PROD" -> prodUrl
            "PREPROD" -> preprodUrl
            else -> devUrl
        }
    }

    fun getEnv(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("env", "DEV")
    }
    var prefs: SharedPreferences? = null
}
