package com.example.mccagent.workers

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mccagent.config.ServiceConfig
import com.example.mccagent.services.PersistentSmsService
import java.util.concurrent.TimeUnit

object SmsWorkScheduler {
    private const val WORK_SERVICE_WATCHDOG = "sms_service_watchdog"

    fun schedule(context: Context) {
        ServiceConfig.setServiceEnabled(context, true)
        PersistentSmsService.start(context)
        scheduleWatchdog(context)
    }

    fun ejecutarSincronizacionInmediata(context: Context) {
        PersistentSmsService.requestImmediateSync(context)
    }

    fun stop(context: Context) {
        PersistentSmsService.stop(context)
        WorkManager.getInstance(context).cancelUniqueWork(WORK_SERVICE_WATCHDOG)
    }

    private fun scheduleWatchdog(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<ServiceWatchdogWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_SERVICE_WATCHDOG,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }
}
