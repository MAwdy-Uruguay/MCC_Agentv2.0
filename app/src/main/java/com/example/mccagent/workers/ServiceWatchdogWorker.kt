package com.example.mccagent.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mccagent.config.ServiceConfig
import com.example.mccagent.services.PersistentSmsService

class ServiceWatchdogWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        if (ServiceConfig.isServiceEnabled(applicationContext)) {
            PersistentSmsService.start(applicationContext)
        }
        return Result.success()
    }
}
