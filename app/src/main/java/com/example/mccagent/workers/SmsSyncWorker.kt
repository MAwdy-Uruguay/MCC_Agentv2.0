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
import com.example.mccagent.utils.SmsCorrelationKeyFactory
import kotlinx.coroutines.delay

class SmsSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val hasSms = context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
        if (!hasSms) {
            Log.w("SmsSyncWorker", "Dispositivo sin capacidad SMS; se omite ciclo")
            return Result.success()
        }

        return try {
            val repository = MessageRepositoryImpl(context)
            val messages = repository.getPendingMessages()
            Log.d("SmsSyncWorker", "Mensajes pendientes recibidos: ${messages.size}")

            for (msg in messages) {
                // Marcamos en progreso para reducir riesgo de reenvío por reintentos.
                val marcadoEnProgreso = repository.updateMessageStatus(msg.mid, "ENVIANDO")
                if (marcadoEnProgreso) {
                    sendSMS(context, msg.mid, msg.recipient, msg.body)
                    delay(1200)
                } else {
                    Log.w("SmsSyncWorker", "No se pudo marcar el mensaje en progreso; se evita duplicado")
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("SmsSyncWorker", "Fallo en sincronización de SMS", e)
            Result.retry()
        }
    }

    private fun sendSMS(context: Context, mid: String, phone: String, body: String) {
        val intent = Intent(context, SmsSentReceiver::class.java)
            .setAction(SmsCorrelationKeyFactory.accionConfirmacion(mid))
            .putExtra("mid", mid)
        val requestCode = SmsCorrelationKeyFactory.requestCode(mid)
        val sentIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            SmsManager.getDefault().sendTextMessage(phone, null, body, sentIntent, null)
            Log.i("SmsSyncWorker", "SMS despachado para confirmación de entrega")
        } catch (e: Exception) {
            Log.e("SmsSyncWorker", "Error al despachar SMS", e)
        }
    }
}
