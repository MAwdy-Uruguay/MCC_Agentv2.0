package com.example.mccagent.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.mccagent.config.ServiceConfig

object ServiceHealthManager {
    private const val ERROR_THRESHOLD = 3
    private const val ALERT_COOLDOWN_MS = 15 * 60 * 1000L
    private const val ALERT_ERROR = "ERROR"
    private const val ALERT_RECOVERED = "RECOVERED"

    fun processSyncResult(context: Context, result: SyncResult): ServiceConfig.HealthState {
        val previousState = ServiceConfig.getHealthState(context)
        val failStreak = if (result.success) 0 else ServiceConfig.getFailStreak(context) + 1
        ServiceConfig.setFailStreak(context, failStreak)

        val newState = when {
            result.success -> ServiceConfig.HealthState.OK
            failStreak >= ERROR_THRESHOLD -> ServiceConfig.HealthState.ERROR
            else -> ServiceConfig.HealthState.DEGRADED
        }

        ServiceConfig.setHealthState(context, newState)
        ServiceConfig.setHealthMessage(
            context,
            when (newState) {
                ServiceConfig.HealthState.OK -> ""
                ServiceConfig.HealthState.DEGRADED -> result.message
                ServiceConfig.HealthState.ERROR -> result.message
                ServiceConfig.HealthState.UNKNOWN -> result.message
            }
        )

        when {
            newState == ServiceConfig.HealthState.ERROR && previousState != ServiceConfig.HealthState.ERROR -> {
                notifyBySmsIfNeeded(
                    context = context,
                    kind = ALERT_ERROR,
                    message = "MCC Agent alerta: servicio en ERROR. ${result.message}"
                )
            }
            newState == ServiceConfig.HealthState.OK &&
                previousState == ServiceConfig.HealthState.ERROR -> {
                notifyBySmsIfNeeded(
                    context = context,
                    kind = ALERT_RECOVERED,
                    message = "MCC Agent recuperado: servicio nuevamente operativo."
                )
            }
        }

        return newState
    }

    private fun notifyBySmsIfNeeded(
        context: Context,
        kind: String,
        message: String,
    ) {
        if (!ServiceConfig.areAlertsEnabled(context)) return

        val phone = ServiceConfig.getAlertPhone(context)
        if (phone.isBlank()) return

        val now = System.currentTimeMillis()
        val lastAlertAt = ServiceConfig.getLastAlertAt(context)
        val lastAlertKind = ServiceConfig.getLastAlertKind(context)
        val stillCoolingDown = now - lastAlertAt < ALERT_COOLDOWN_MS

        if (stillCoolingDown && lastAlertKind == kind) {
            return
        }

        if (!hasSmsPermission(context)) {
            Log.w("ServiceHealthManager", "No se pudo enviar SMS de alerta: falta permiso SEND_SMS")
            return
        }

        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }

            if (smsManager == null) {
                Log.w("ServiceHealthManager", "No se obtuvo SmsManager para la alerta remota")
                return
            }

            smsManager.sendTextMessage(phone, null, message, null, null)
            ServiceConfig.setLastAlertAt(context, now)
            ServiceConfig.setLastAlertKind(context, kind)
            Log.i("ServiceHealthManager", "SMS de alerta enviado a $phone")
        } catch (e: Exception) {
            Log.e("ServiceHealthManager", "Error al enviar SMS de alerta", e)
        }
    }

    private fun hasSmsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
