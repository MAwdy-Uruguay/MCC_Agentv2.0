package com.example.mccagent.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, context: Context) {
    val context = LocalContext.current
    var baseUrl by remember { mutableStateOf("") }
    var isProd by remember { mutableStateOf(false) }

    // 🔄 Cargar valores desde SharedPreferences
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
        baseUrl = prefs.getString("base_url", "http://192.168.8.151:5000/api/") ?: ""
        isProd = prefs.getString("env", "DEV") == "PROD"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚙️ Configuración") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                label = { Text("URL base del API") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Modo Producción")
                Switch(
                    checked = isProd,
                    onCheckedChange = { isProd = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putString("base_url", baseUrl)
                        .putString("env", if (isProd) "PROD" else "DEV")
                        .apply()

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("💾 Guardar cambios")
            }
        }
    }
}
