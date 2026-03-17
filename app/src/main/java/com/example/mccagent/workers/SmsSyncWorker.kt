package com.example.mccagent.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mccagent.services.SmsSyncEngine

class SmsSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val result = SmsSyncEngine.runCycle(applicationContext, playHeartbeat = false)
        return if (result.success) Result.success() else Result.retry()
    }
}
