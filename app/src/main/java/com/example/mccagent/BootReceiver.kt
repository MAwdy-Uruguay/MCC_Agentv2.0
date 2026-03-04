package com.example.mccagent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mccagent.workers.SmsWorkScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
            val flujoActivo = prefs.getBoolean("sms_service_running", false)

            if (flujoActivo) {
                Log.d("BootReceiver", "Reinicio detectado; se restablece flujo único de SMS")
                SmsWorkScheduler.schedule(context)
            } else {
                Log.d("BootReceiver", "Reinicio detectado sin flujo activo; no se agenda sincronización")
            }
        }
    }
}
