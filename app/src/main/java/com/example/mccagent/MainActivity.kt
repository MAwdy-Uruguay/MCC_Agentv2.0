package com.example.mccagent

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.mccagent.config.ApiConfig
import com.example.mccagent.services.BatteryOptimizationHelper
import com.example.mccagent.ui.navigation.AppNavigation
import com.example.mccagent.ui.theme.MCCAgentTheme
import com.example.mccagent.workers.SmsWorkScheduler

class MainActivity : ComponentActivity() {
    private val permisos: Array<String>
        get() = buildList {
            add(Manifest.permission.SEND_SMS)
            add(Manifest.permission.RECEIVE_SMS)
            add(Manifest.permission.READ_SMS)
            add(Manifest.permission.READ_PHONE_STATE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()

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
            Toast.makeText(this, "Permisos denegados", Toast.LENGTH_LONG).show()
        }
    }

    private fun iniciarServicioSMS() {
        if (!BatteryOptimizationHelper.isIgnoringBatteryOptimizations(this)) {
            BatteryOptimizationHelper.requestIgnoreBatteryOptimizations(this)
        }
        Log.d("MainActivity", "Iniciando servicio SMS persistente")
        SmsWorkScheduler.schedule(this)
    }
}
