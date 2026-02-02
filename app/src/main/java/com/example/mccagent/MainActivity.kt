package com.example.mccagent

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.mccagent.services.SMSService
import com.example.mccagent.ui.theme.MCCAgentTheme
import android.util.Log
import com.example.mccagent.config.ApiConfig
import com.example.mccagent.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    private val permisos = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.READ_PHONE_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ApiConfig.prefs = getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
        requestPermissionsIfNeeded()

        setContent {
            MCCAgentTheme {
                AppNavigation(this@MainActivity)
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        val notGranted = permisos.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            requestPermissionLauncher.launch(notGranted.toTypedArray())
        } else {
            iniciarServicioSMS()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.all { it.value }) {
            iniciarServicioSMS()
        } else {
            Toast.makeText(this, "🚫 Permisos denegados", Toast.LENGTH_LONG).show()
        }
    }

    private fun iniciarServicioSMS() {
        Log.d("MainActivity", "🚀 Iniciando servicio SMS")
        val intent = Intent(this, SMSService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    fun handleLogout(context: Context) {

        val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("token").apply()

        val stopIntent = Intent(context, SMSService::class.java)
        context.stopService(stopIntent)

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
