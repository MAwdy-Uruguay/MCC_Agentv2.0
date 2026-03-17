package com.example.mccagent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mccagent.config.ServiceConfig
import com.example.mccagent.workers.SmsWorkScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                if (ServiceConfig.isServiceEnabled(context)) {
                    Log.d("BootReceiver", "Evento de sistema detectado; se restablece servicio persistente")
                    SmsWorkScheduler.schedule(context)
                } else {
                    Log.d("BootReceiver", "Evento de sistema detectado sin servicio activo")
                }
            }
            else -> Unit
        }
    }
}
