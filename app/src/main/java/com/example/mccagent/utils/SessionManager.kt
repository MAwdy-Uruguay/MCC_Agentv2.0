package com.example.mccagent.utils

import android.content.Context
import android.content.Intent
import com.example.mccagent.services.SMSService

object SessionManager {
    fun logout(context: Context) {
        val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("token").apply()

        val stopIntent = Intent(context, SMSService::class.java)
        context.stopService(stopIntent)
    }
}
