package com.example.mccagent.utils

object SmsCorrelationKeyFactory {
    fun requestCode(mid: String): Int = mid.hashCode()

    fun accionConfirmacion(mid: String): String = "com.example.mccagent.SMS_SENT.$mid"

    fun claveCorrelacion(mid: String): String = "${requestCode(mid)}|${accionConfirmacion(mid)}"
}
