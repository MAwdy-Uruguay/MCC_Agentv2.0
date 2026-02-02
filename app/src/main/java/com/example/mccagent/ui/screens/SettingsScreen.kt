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
import android.util.Patterns

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, context: Context) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)

    // Estados para cada URL
    var prodUrl by remember { mutableStateOf("") }
    var preprodUrl by remember { mutableStateOf("") }
    var devUrl by remember { mutableStateOf("") }

    // Estado para el entorno seleccionado
    var selectedEnv by remember { mutableStateOf("DEV") }

    // Cargar valores guardados al abrir pantalla
    LaunchedEffect(Unit) {
        prodUrl = prefs.getString("url_prod","")!!
        preprodUrl = prefs.getString("url_preprod","https://suy002001-dev/mccserver-dev/api/")!!
        devUrl = prefs.getString("url_dev","https://localhost/novalid/api/")!!
        selectedEnv = prefs.getString("env", "DEV") ?: "DEV"
    }

    fun isValidUrl(value: String): Boolean {
        return value.isNotBlank() && Patterns.WEB_URL.matcher(value).matches()
    }

    val prodUrlError = prodUrl.isNotBlank() && !isValidUrl(prodUrl)
    val preprodUrlError = preprodUrl.isNotBlank() && !isValidUrl(preprodUrl)
    val devUrlError = devUrl.isNotBlank() && !isValidUrl(devUrl)
    val hasAnyError = prodUrlError || preprodUrlError || devUrlError

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
            // Producción
            OutlinedTextField(
                value = prodUrl,
                onValueChange = { prodUrl = it },
                label = { Text("🌐 URL Producción") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = prodUrlError,
                supportingText = {
                    if (prodUrlError) {
                        Text("URL inválida")
                    }
                }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedEnv == "PROD",
                    onClick = { selectedEnv = "PROD" }
                )
                Text("Usar Producción")
            }

            Spacer(Modifier.height(16.dp))

            // Preproducción
            OutlinedTextField(
                value = preprodUrl,
                onValueChange = { preprodUrl = it },
                label = { Text("🛠 URL Preproducción") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = preprodUrlError,
                supportingText = {
                    if (preprodUrlError) {
                        Text("URL inválida")
                    }
                }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedEnv == "PREPROD",
                    onClick = { selectedEnv = "PREPROD" }
                )
                Text("Usar Preproducción")
            }

            Spacer(Modifier.height(16.dp))

            // Desarrollo
            OutlinedTextField(
                value = devUrl,
                onValueChange = { devUrl = it },
                label = { Text("💻 URL Desarrollo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = devUrlError,
                supportingText = {
                    if (devUrlError) {
                        Text("URL inválida")
                    }
                }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedEnv == "DEV",
                    onClick = { selectedEnv = "DEV" }
                )
                Text("Usar Desarrollo")
            }

            Spacer(Modifier.height(24.dp))

            val activeUrl = when (selectedEnv) {
                "PROD" -> prodUrl
                "PREPROD" -> preprodUrl
                else -> devUrl
            }
            Text(
                text = "URL activa: $activeUrl",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    prefs.edit()
                        .putString("url_prod", prodUrl)
                        .putString("url_preprod", preprodUrl)
                        .putString("url_dev", devUrl)
                        .putString("env", selectedEnv)
                        // guardar también la url activa según env
                        .putString(
                            "base_url",
                            when (selectedEnv) {
                                "PROD" -> prodUrl
                                "PREPROD" -> preprodUrl
                                else -> devUrl
                            }
                        )
                        .apply()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !hasAnyError
            ) {
                Text("💾 Guardar configuración")
            }
        }
    }
}
