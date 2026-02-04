package com.example.mccagent.workers

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SmsWorkScheduler {
    private const val WORK_NAME = "sms_sync"

    fun schedule(context: Context, delaySeconds: Long = 0L) {
        val request = OneTimeWorkRequestBuilder<SmsSyncWorker>()
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)

        context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("sms_service_running", true)
            .apply()
    }

    fun stop(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("sms_service_running", false)
            .apply()
    }
}
