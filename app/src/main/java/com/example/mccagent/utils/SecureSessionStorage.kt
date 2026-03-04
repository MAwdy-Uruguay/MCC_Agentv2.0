package com.example.mccagent.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureSessionStorage {
    private const val PREFS_LEGACY = "mcc_prefs"
    private const val PREFS_SEGURO = "mcc_secure_prefs"
    private const val KEY_TOKEN = "token"

    fun guardarToken(context: Context, token: String) {
        val prefs = obtenerPreferenciasSeguras(context)
        prefs.edit().putString(KEY_TOKEN, token).apply()

        // Eliminamos el token legado en texto plano si existe.
        obtenerPreferenciasLegadas(context).edit().remove(KEY_TOKEN).apply()
    }

    fun obtenerToken(context: Context): String? {
        val prefsSeguras = obtenerPreferenciasSeguras(context)
        val tokenSeguro = prefsSeguras.getString(KEY_TOKEN, null)
        if (!tokenSeguro.isNullOrBlank()) {
            return tokenSeguro
        }

        return migrarTokenLegado(context, prefsSeguras)
    }

    fun limpiarSesion(context: Context) {
        obtenerPreferenciasSeguras(context).edit().remove(KEY_TOKEN).apply()
        obtenerPreferenciasLegadas(context).edit().remove(KEY_TOKEN).apply()
    }

    private fun migrarTokenLegado(
        context: Context,
        prefsSeguras: SharedPreferences,
    ): String? {
        val prefsLegadas = obtenerPreferenciasLegadas(context)
        val tokenLegado = prefsLegadas.getString(KEY_TOKEN, null)

        if (!tokenLegado.isNullOrBlank()) {
            prefsSeguras.edit().putString(KEY_TOKEN, tokenLegado).apply()
            prefsLegadas.edit().remove(KEY_TOKEN).apply()
            Log.i("SecureSessionStorage", "Token legado migrado a almacenamiento cifrado")
            return tokenLegado
        }

        return null
    }

    private fun obtenerPreferenciasSeguras(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREFS_SEGURO,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        } catch (e: Exception) {
            Log.e("SecureSessionStorage", "No se pudieron abrir preferencias cifradas", e)
            obtenerPreferenciasLegadas(context)
        }
    }

    private fun obtenerPreferenciasLegadas(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_LEGACY, Context.MODE_PRIVATE)
    }
}
