package com.example.mccagent.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.mccagent.MainActivity
import com.example.mccagent.R
import com.example.mccagent.config.ServiceConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PersistentSmsService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()
    private var loopJob: Job? = null
    private var currentStatus: String = "Inicializando"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(currentStatus))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopSelfSafely()
            ACTION_SYNC_NOW -> {
                if (loopJob?.isActive == true) {
                    triggerImmediateSync()
                } else {
                    startLoopIfNeeded()
                }
            }
            else -> startLoopIfNeeded()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        loopJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startLoopIfNeeded() {
        if (loopJob?.isActive == true) return

        ServiceConfig.setServiceEnabled(this, true)
        loopJob = serviceScope.launch {
            while (true) {
                runSyncCycle()
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private fun triggerImmediateSync() {
        serviceScope.launch {
            runSyncCycle()
        }
    }

    private suspend fun runSyncCycle() {
        syncMutex.withLock {
            val result = SmsSyncEngine.runCycle(this, playHeartbeat = true)
            val healthState = ServiceHealthManager.processSyncResult(this, result)
            HeartbeatPlayer.playForState(this, healthState)
            currentStatus = buildStatusText(result)
            updateNotification(currentStatus)
        }
    }

    private fun buildStatusText(result: SyncResult): String {
        val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        return when (ServiceConfig.getHealthState(this)) {
            ServiceConfig.HealthState.OK -> "Activo. Ultima consulta $hora. Pendientes: ${result.pendingCount}"
            ServiceConfig.HealthState.DEGRADED -> "Atencion. Reintentos detectados $hora: ${result.message}"
            ServiceConfig.HealthState.ERROR -> "Error operativo $hora: ${result.message}"
            ServiceConfig.HealthState.UNKNOWN -> "Inicializando monitoreo"
        }
    }

    private fun buildNotification(contentText: String): Notification {
        val openIntent = PendingIntent.getActivity(
            this,
            1,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("MCC Agent activo")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(contentText: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(contentText))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "MCC Agent Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Mantiene activa la sincronizacion de SMS"
            setShowBadge(false)
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun stopSelfSafely() {
        Log.i("PersistentSmsService", "Servicio detenido manualmente")
        ServiceConfig.setServiceEnabled(this, false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {
        private const val CHANNEL_ID = "mcc_agent_foreground"
        private const val NOTIFICATION_ID = 7001
        private const val POLL_INTERVAL_MS = 60_000L
        private const val ACTION_SYNC_NOW = "com.example.mccagent.action.SYNC_NOW"
        private const val ACTION_STOP = "com.example.mccagent.action.STOP_SERVICE"

        fun start(context: Context) {
            val intent = Intent(context, PersistentSmsService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun requestImmediateSync(context: Context) {
            val intent = Intent(context, PersistentSmsService::class.java).apply {
                action = ACTION_SYNC_NOW
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, PersistentSmsService::class.java).apply {
                action = ACTION_STOP
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}
