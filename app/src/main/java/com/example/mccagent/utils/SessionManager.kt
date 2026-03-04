package com.example.mccagent.utils

import android.content.Context
import com.example.mccagent.workers.SmsWorkScheduler

object SessionManager {
    fun logout(context: Context) {
        SecureSessionStorage.limpiarSesion(context)
        SmsWorkScheduler.stop(context)
    }
}
