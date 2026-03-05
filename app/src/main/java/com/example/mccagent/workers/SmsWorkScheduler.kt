package com.example.mccagent.workers

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SmsWorkScheduler {
    private const val WORK_NAME = "sms_sync_periodico"
    private const val WORK_UNICO_INMEDIATO = "sms_sync_inmediato"
<<<<<<< codex/investigar-envio-de-sms-pendientes-o0b2fx
    private const val WORK_UNICO_CADA_MINUTO = "sms_sync_cada_minuto"
=======
>>>>>>> dev1.2

    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<SmsSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)

        ejecutarSincronizacionInmediata(context)
<<<<<<< codex/investigar-envio-de-sms-pendientes-o0b2fx
        programarSiguienteSincronizacion(context)
=======
>>>>>>> dev1.2

        context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("sms_service_running", true)
            .apply()
    }

    fun ejecutarSincronizacionInmediata(context: Context) {
        val request = OneTimeWorkRequestBuilder<SmsSyncWorker>()
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_UNICO_INMEDIATO, ExistingWorkPolicy.REPLACE, request)
    }

<<<<<<< codex/investigar-envio-de-sms-pendientes-o0b2fx
    fun programarSiguienteSincronizacion(context: Context) {
        val request = OneTimeWorkRequestBuilder<SmsSyncWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_UNICO_CADA_MINUTO, ExistingWorkPolicy.REPLACE, request)
    }

    fun stop(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(WORK_UNICO_INMEDIATO)
        WorkManager.getInstance(context).cancelUniqueWork(WORK_UNICO_CADA_MINUTO)
=======
    fun stop(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(WORK_UNICO_INMEDIATO)
>>>>>>> dev1.2

        context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("sms_service_running", false)
            .apply()
    }
}
