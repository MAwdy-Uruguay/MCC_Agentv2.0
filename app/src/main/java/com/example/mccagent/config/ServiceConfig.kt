package com.example.mccagent.config

import android.content.Context

object ServiceConfig {
    private const val PREFS_NAME = "mcc_prefs"
    private const val KEY_SERVICE_ENABLED = "sms_service_running"
    private const val KEY_HEARTBEAT_ENABLED = "heartbeat_enabled"
    private const val KEY_HEARTBEAT_VOLUME = "heartbeat_volume"
    private const val KEY_LAST_SYNC = "last_sync_epoch_ms"

    fun isServiceEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_SERVICE_ENABLED, true)
    }

    fun setServiceEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_SERVICE_ENABLED, enabled).apply()
    }

    fun isHeartbeatEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_HEARTBEAT_ENABLED, true)
    }

    fun setHeartbeatEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_HEARTBEAT_ENABLED, enabled).apply()
    }

    fun getHeartbeatVolume(context: Context): Int {
        return prefs(context).getInt(KEY_HEARTBEAT_VOLUME, 15).coerceIn(0, 100)
    }

    fun setHeartbeatVolume(context: Context, volume: Int) {
        prefs(context).edit().putInt(KEY_HEARTBEAT_VOLUME, volume.coerceIn(0, 100)).apply()
    }

    fun getLastSyncEpochMs(context: Context): Long {
        return prefs(context).getLong(KEY_LAST_SYNC, 0L)
    }

    fun setLastSyncEpochMs(context: Context, value: Long) {
        prefs(context).edit().putLong(KEY_LAST_SYNC, value).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
