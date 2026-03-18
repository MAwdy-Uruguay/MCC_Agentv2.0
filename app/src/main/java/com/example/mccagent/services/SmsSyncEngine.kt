package com.example.mccagent.services

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.mccagent.config.ServiceConfig
import com.example.mccagent.repository.ClientRepositoryImpl
import com.example.mccagent.repository.MessageRepositoryImpl
import com.example.mccagent.utils.SmsCorrelationKeyFactory
import kotlinx.coroutines.delay

data class SyncResult(
    val success: Boolean,
    val message: String,
    val pendingCount: Int = 0,
)

object SmsSyncEngine {
    suspend fun runCycle(context: Context, playHeartbeat: Boolean): SyncResult {
        val hasSms = context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
        if (!hasSms) {
            return SyncResult(false, "Dispositivo sin capacidad SMS")
        }

        val permisoSms = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
        if (!permisoSms) {
            return SyncResult(false, "Permiso SEND_SMS no concedido")
        }

        return try {
            val clientRepository = ClientRepositoryImpl(context)
            val clientHealthResponse = clientRepository.getClient()
            if (!clientHealthResponse.isSuccessful) {
                return SyncResult(
                    success = false,
                    message = "Error validando cliente/API: HTTP ${clientHealthResponse.code()}",
                    pendingCount = 0
                )
            }

            val repository = MessageRepositoryImpl(context)
            val messages = repository.getPendingMessages()
            Log.d("SmsSyncEngine", "Mensajes pendientes recibidos: ${messages.size}")

            for (msg in messages) {
                val marcadoEnProgreso = repository.updateMessageStatus(msg.mid, "ENVIANDO")
                if (!marcadoEnProgreso) {
                    Log.w(
                        "SmsSyncEngine",
                        "No se pudo marcar en ENVIANDO el mensaje ${msg.mid}; se intentara despacho igualmente"
                    )
                }

                sendSms(context, msg.mid, msg.recipient, msg.body)
                delay(1200)
            }

            ServiceConfig.setLastSyncEpochMs(context, System.currentTimeMillis())
            SyncResult(true, "Consulta completada", messages.size)
        } catch (e: Exception) {
            Log.e("SmsSyncEngine", "Fallo en sincronizacion de SMS", e)
            SyncResult(false, e.message ?: "Fallo en sincronizacion")
        }
    }

    private fun sendSms(context: Context, mid: String, phone: String, body: String) {
        val intent = Intent(context, SmsSentReceiver::class.java)
            .setAction(SmsCorrelationKeyFactory.accionConfirmacion(mid))
            .putExtra("mid", mid)

        val sentIntent = PendingIntent.getBroadcast(
            context,
            SmsCorrelationKeyFactory.requestCode(mid),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }

            if (smsManager == null) {
                Log.e("SmsSyncEngine", "No se obtuvo instancia de SmsManager. mid=$mid")
                return
            }

            smsManager.sendTextMessage(phone, null, body, sentIntent, null)
            Log.i("SmsSyncEngine", "SMS despachado para confirmacion de entrega. mid=$mid destino=$phone")
        } catch (e: Exception) {
            Log.e("SmsSyncEngine", "Error al despachar SMS. mid=$mid destino=$phone", e)
        }
    }
}
