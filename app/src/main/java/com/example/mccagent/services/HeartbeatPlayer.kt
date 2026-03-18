package com.example.mccagent.services

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import com.example.mccagent.config.ServiceConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

object HeartbeatPlayer {
    fun playForState(
        context: android.content.Context,
        state: ServiceConfig.HealthState,
    ) {
        if (!ServiceConfig.isHeartbeatEnabled(context)) return

        val volume = ServiceConfig.getHeartbeatVolume(context)
        if (volume <= 0) return

        try {
            val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, volume)
            try {
                when (state) {
                    ServiceConfig.HealthState.OK -> tone.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
                    ServiceConfig.HealthState.DEGRADED -> runBlocking {
                        tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 90)
                        delay(160)
                        tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 90)
                    }
                    ServiceConfig.HealthState.ERROR -> runBlocking {
                        repeat(3) { index ->
                            tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 140)
                            if (index < 2) delay(180)
                        }
                    }
                    ServiceConfig.HealthState.UNKNOWN -> tone.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
                }
            } finally {
                tone.release()
            }
        } catch (e: Exception) {
            Log.w("HeartbeatPlayer", "No se pudo reproducir el heartbeat", e)
        }
    }
}
