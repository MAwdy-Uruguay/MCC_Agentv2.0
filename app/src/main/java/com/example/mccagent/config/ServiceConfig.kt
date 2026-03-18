package com.example.mccagent.config

import android.content.Context

object ServiceConfig {
    private const val PREFS_NAME = "mcc_prefs"
    private const val KEY_SERVICE_ENABLED = "sms_service_running"
    private const val KEY_HEARTBEAT_ENABLED = "heartbeat_enabled"
    private const val KEY_HEARTBEAT_VOLUME = "heartbeat_volume"
    private const val KEY_LAST_SYNC = "last_sync_epoch_ms"
    private const val KEY_ALERT_PHONE = "alert_phone"
    private const val KEY_ALERTS_ENABLED = "alerts_enabled"
    private const val KEY_FAIL_STREAK = "service_fail_streak"
    private const val KEY_HEALTH_STATE = "service_health_state"
    private const val KEY_HEALTH_MESSAGE = "service_health_message"
    private const val KEY_LAST_ALERT_AT = "service_last_alert_at"
    private const val KEY_LAST_ALERT_KIND = "service_last_alert_kind"

    enum class HealthState {
        OK,
        DEGRADED,
        ERROR,
        UNKNOWN
    }

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

    fun getAlertPhone(context: Context): String {
        return prefs(context).getString(KEY_ALERT_PHONE, "")?.trim().orEmpty()
    }

    fun setAlertPhone(context: Context, phone: String) {
        prefs(context).edit().putString(KEY_ALERT_PHONE, normalizeAlertPhone(phone)).apply()
    }

    fun areAlertsEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_ALERTS_ENABLED, false)
    }

    fun setAlertsEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ALERTS_ENABLED, enabled).apply()
    }

    fun getFailStreak(context: Context): Int {
        return prefs(context).getInt(KEY_FAIL_STREAK, 0)
    }

    fun setFailStreak(context: Context, count: Int) {
        prefs(context).edit().putInt(KEY_FAIL_STREAK, count.coerceAtLeast(0)).apply()
    }

    fun getHealthState(context: Context): HealthState {
        val raw = prefs(context).getString(KEY_HEALTH_STATE, HealthState.UNKNOWN.name)
        return HealthState.entries.firstOrNull { it.name == raw } ?: HealthState.UNKNOWN
    }

    fun setHealthState(context: Context, state: HealthState) {
        prefs(context).edit().putString(KEY_HEALTH_STATE, state.name).apply()
    }

    fun getHealthMessage(context: Context): String {
        return prefs(context).getString(KEY_HEALTH_MESSAGE, "") ?: ""
    }

    fun setHealthMessage(context: Context, message: String) {
        prefs(context).edit().putString(KEY_HEALTH_MESSAGE, message).apply()
    }

    fun getLastAlertAt(context: Context): Long {
        return prefs(context).getLong(KEY_LAST_ALERT_AT, 0L)
    }

    fun setLastAlertAt(context: Context, value: Long) {
        prefs(context).edit().putLong(KEY_LAST_ALERT_AT, value).apply()
    }

    fun getLastAlertKind(context: Context): String {
        return prefs(context).getString(KEY_LAST_ALERT_KIND, "") ?: ""
    }

    fun setLastAlertKind(context: Context, kind: String) {
        prefs(context).edit().putString(KEY_LAST_ALERT_KIND, kind).apply()
    }

    fun normalizeAlertPhone(phone: String): String {
        val raw = phone.trim().replace(" ", "").replace("-", "")
        if (raw.isBlank()) return ""

        return when {
            raw.startsWith("+") -> raw
            raw.startsWith("598") -> "+$raw"
            raw.startsWith("0") && raw.length == 9 -> "+598${raw.drop(1)}"
            raw.length == 8 || raw.length == 9 -> "+598${raw.trimStart('0')}"
            else -> raw
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
