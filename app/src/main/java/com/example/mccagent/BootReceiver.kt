package com.example.mccagent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mccagent.workers.SmsWorkScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "📡 Dispositivo reiniciado. Reprogramando sync de SMS...")
            SmsWorkScheduler.schedule(context)
        }
    }
}
