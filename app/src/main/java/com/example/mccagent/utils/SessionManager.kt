package com.example.mccagent.utils

import android.content.Context
import com.example.mccagent.workers.SmsWorkScheduler

object SessionManager {
    fun logout(context: Context) {
        val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("token").apply()
        SmsWorkScheduler.stop(context)
    }
}
