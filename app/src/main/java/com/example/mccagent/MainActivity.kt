package com.example.mccagent

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.mccagent.utils.SessionManager
import com.example.mccagent.workers.SmsWorkScheduler
import com.example.mccagent.ui.theme.MCCAgentTheme
import android.util.Log
import com.example.mccagent.config.ApiConfig
import com.example.mccagent.ui.navigation.AppNavigation
import com.example.mccagent.utils.SecureSessionStorage

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
        SecureSessionStorage.obtenerToken(this)
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
        SmsWorkScheduler.schedule(this)
    }

    fun handleLogout(context: Context) {
        SessionManager.logout(context)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
