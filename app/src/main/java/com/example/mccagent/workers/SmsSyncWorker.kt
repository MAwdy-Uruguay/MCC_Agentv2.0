package com.example.mccagent.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mccagent.repository.MessageRepositoryImpl
import com.example.mccagent.services.SmsSentReceiver
import kotlinx.coroutines.delay

class SmsSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val hasSms = context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
        if (!hasSms) {
            Log.w("SmsSyncWorker", "🚫 Dispositivo no soporta SMS")
            SmsWorkScheduler.schedule(context, delaySeconds = 60)
            return Result.success()
        }

        return try {
            val repository = MessageRepositoryImpl(context)
            val messages = repository.getPendingMessages()
            Log.d("SmsSyncWorker", "📨 Mensajes pendientes: ${messages.size}")

            for (msg in messages) {
                sendSMS(context, msg.mid, msg.recipient, msg.body)
                delay(2000)
            }

            SmsWorkScheduler.schedule(context, delaySeconds = 10)
            Result.success()
        } catch (e: Exception) {
            Log.e("SmsSyncWorker", "❌ Error en sync: ${e.message}", e)
            SmsWorkScheduler.schedule(context, delaySeconds = 30)
            Result.retry()
        }
    }

    private fun sendSMS(context: Context, mid: String, phone: String, body: String) {
        val intent = Intent(context, SmsSentReceiver::class.java).putExtra("mid", mid)
        val sentIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            SmsManager.getDefault().sendTextMessage(phone, null, body, sentIntent, null)
            Log.i("SmsSyncWorker", "✉️ Enviado a $phone (pendiente confirmación)")
        } catch (e: Exception) {
            Log.e("SmsSyncWorker", "❌ Error al enviar SMS: ${e.message}", e)
        }
    }
}
