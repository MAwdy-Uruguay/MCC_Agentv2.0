package com.example.mccagent.services

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.mccagent.R
import com.example.mccagent.repository.MessageRepositoryImpl
import kotlinx.coroutines.*
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings

class SMSService : Service() {

    private val CHANNEL_ID = "MCCAgentSMSChannel"
    private lateinit var messageRepository: MessageRepositoryImpl

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var isRunning = false

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        messageRepository = MessageRepositoryImpl(this)

        // ✅ Registro dinámico seguro para Android 13+
 //       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            registerReceiver(
//                smsSentReceiver,
//                IntentFilter("SMS_SENT"),
//                Context.RECEIVER_EXPORTED
//            )
//        } else {
//            registerReceiver(smsSentReceiver, IntentFilter("SMS_SENT"))
//        }

        getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("sms_service_running", true).apply()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SMSService", "🚀 Servicio iniciado")

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MCC Agent activo")
            .setContentText("Enviando mensajes SMS pendientes")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        startForeground(1, notification)

        if (!isRunning) {
            isRunning = true
            scope.launch {
                while (true) {
                    try {
                        val hasSms = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
                        if (!hasSms) {
                            Log.w("SMSService", "🚫 Dispositivo no soporta SMS")
                            delay(10000)
                            continue
                        }

                        val messages = messageRepository.getPendingMessages()
                        Log.d("SMSService", "📨 Mensajes pendientes: ${messages.size}")

                        for (msg in messages) {
                            sendSMS(msg.mid, msg.recipient, msg.body)
                            delay(2000) // evita saturar
                        }
                    } catch (e: Exception) {
                        Log.e("SMSService", "❌ Error en loop de envío: ${e.message}", e)
                    }

                    delay(10000) // espera antes de volver a consultar
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        try {
           // unregisterReceiver(smsSentReceiver)
        } catch (_: Exception) { }

        scope.cancel()
        isRunning = false
        getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("sms_service_running", false).apply()

        Log.d("SMSService", "🛑 Servicio detenido")
        super.onDestroy()
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MCC Agent SMS Channel V2",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(true)
                enableVibration(true )
                setSound(null, null)
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    .setData(Uri.parse("package:$packageName"))
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }

    private fun sendSMS(mid: String, phone: String, body: String) {
        val intent = Intent(this, SmsSentReceiver::class.java).putExtra("mid", mid)
        val sentIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            SmsManager.getDefault().sendTextMessage(phone, null, body, sentIntent, null)
            Log.i("SMSService", "✉️ Enviado a $phone (pendiente confirmación)")
        } catch (e: Exception) {
            Log.e("SMSService", "❌ Error al enviar SMS: ${e.message}", e)
        }
    }
}


//class SMSService : Service() {
//
//    private val CHANNEL_ID = "MCCAgentSMSChannel"
//    private lateinit var messageRepository: MessageRepositoryImpl
//
//    private val job = SupervisorJob()
//
//    private val scope = CoroutineScope(Dispatchers.IO+job)
//    private var isRunning = false
//
//
//    override fun onCreate() {
//        super.onCreate()
//        createNotificationChannel()
//        messageRepository = MessageRepositoryImpl(this)
//
//        ContextCompat.registerReceiver(
//            this,
//            smsSentReceiver,
//            IntentFilter("SMS_SENT"),
//            ContextCompat.RECEIVER_NOT_EXPORTED
//        )
//
//
//        getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
//            .edit().putBoolean("sms_service_running", true).apply()
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Log.d("SMSService", "🚀 Servicio iniciado")
//
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("MCC Agent activo")
//            .setContentText("Enviando mensajes SMS pendientes")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setOngoing(true)
//            .build()
//
//        startForeground(1, notification)
//
//        if (!isRunning) {
//            isRunning = true
//            scope.launch {
//                while (true) {
//                    try {
//                        val hasSms = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
//                        if (!hasSms) {
//                            Log.w("SMSService", "🚫 Dispositivo no soporta SMS")
//                            delay(10000)
//                            continue
//                        }
//
//                        val messages = messageRepository.getPendingMessages()
//                        Log.d("SMSService", "📨 Mensajes pendientes: ${messages.size}")
//
//                        for (msg in messages) {
//                            sendSMS(msg.mid, msg.recipient, msg.body)
//                            delay(2000)
//                        }
//                    } catch (e: Exception) {
//                        Log.e("SMSService", "❌ Error en loop de envío: ${e.message}")
//                    }
//
//                    delay(10000)
//                }
//            }
//        }
//
//        return START_STICKY
//    }
//
//    override fun onDestroy() {
//        try {
//            unregisterReceiver(smsSentReceiver)
//        } catch (_: Exception) {}
//
//        scope.cancel()
//        isRunning = false
//        getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
//            .edit().putBoolean("sms_service_running", false).apply()
//
//        Log.d("SMSService", "🛑 Servicio detenido")
//        super.onDestroy()
//        job.cancel()
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "MCC Agent SMS Channel",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            getSystemService(NotificationManager::class.java)
//                .createNotificationChannel(channel)
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
//            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
//                    .setData(Uri.parse("package:$packageName"))
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//            }
//        }
//
//    }
//
//    private fun sendSMS(mid: String, phone: String, body: String) {
//        val intent = Intent("SMS_SENT").putExtra("mid", mid)
//        val sentIntent = PendingIntent.getBroadcast(
//            this, mid.hashCode(), intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        try {
//            SmsManager.getDefault().sendTextMessage(phone, null, body, sentIntent, null)
//            Log.i("SMSService", "✉️ Enviado a $phone (pendiente confirmación)")
//        } catch (e: Exception) {
//            Log.e("SMSService", "❌ Error al enviar SMS: ${e.message}")
//        }
//    }
//
//    private val smsSentReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val mid = intent.getStringExtra("mid") ?: return
//            val resultCode = resultCode
//            val status = when (resultCode) {
//                Activity.RESULT_OK -> "ENVIADO"
//                SmsManager.RESULT_ERROR_GENERIC_FAILURE,
//                SmsManager.RESULT_ERROR_NO_SERVICE,
//                SmsManager.RESULT_ERROR_NULL_PDU,
//                SmsManager.RESULT_ERROR_RADIO_OFF -> "FALLIDO"
//                else -> "FALLIDO"
//            }
//
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val repo = MessageRepositoryImpl(context)
//                    repo.updateMessageStatus(mid, status)
//                    Log.i("SMSService", "✅ Estado actualizado: $mid -> $status")
//                } catch (e: Exception) {
//                    Log.e("SMSService", "❌ Error en updateMessageStatus: ${e.message}", e)
//                }
//            }
//        }
//    }
//}
//


