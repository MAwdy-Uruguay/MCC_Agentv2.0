package com.example.mccagent.services

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import com.example.mccagent.config.ServiceConfig

object HeartbeatPlayer {
    fun playIfEnabled(context: android.content.Context) {
        if (!ServiceConfig.isHeartbeatEnabled(context)) return

        val volume = ServiceConfig.getHeartbeatVolume(context)
        if (volume <= 0) return

        try {
            val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, volume)
            try {
                tone.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
            } finally {
                tone.release()
            }
        } catch (e: Exception) {
            Log.w("HeartbeatPlayer", "No se pudo reproducir el heartbeat", e)
        }
    }
}
