package com.example.mccagent.utils

import android.content.Context

object SessionManager {
    fun logout(context: Context) {
        // El flujo de SMS se mantiene activo por requerimiento operativo.
        SecureSessionStorage.limpiarSesion(context)
    }
}
