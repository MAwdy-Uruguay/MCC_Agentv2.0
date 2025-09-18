package com.example.mccagent.services

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import com.example.mccagent.repository.MessageRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsSentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val mid = intent.getStringExtra("mid") ?: return
        val rc = resultCode
        val status = when (rc) {
            Activity.RESULT_OK -> "ENVIADO"
            SmsManager.RESULT_ERROR_GENERIC_FAILURE,
            SmsManager.RESULT_ERROR_NO_SERVICE,
            SmsManager.RESULT_ERROR_NULL_PDU,
            SmsManager.RESULT_ERROR_RADIO_OFF -> "FALLIDO"
            else -> "FALLIDO"
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = MessageRepositoryImpl(context)
                repo.updateMessageStatus(mid, status)
                Log.i("SmsSentReceiver", "✅ Estado actualizado: $mid -> $status")
            } catch (e: Exception) {
                Log.e("SmsSentReceiver", "❌ Error en updateMessageStatus: ${e.message}", e)
            }
        }
    }
}

