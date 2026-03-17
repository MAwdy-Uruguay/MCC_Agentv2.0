package com.example.mccagent.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mccagent.config.ApiConfig
import com.example.mccagent.config.ServiceConfig
import com.example.mccagent.services.BatteryOptimizationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, context: Context) {
    val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)

    var prodUrl by remember { mutableStateOf("") }
    var preprodUrl by remember { mutableStateOf("") }
    var devUrl by remember { mutableStateOf("") }
    var apiKeyHeader by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var selectedEnv by remember { mutableStateOf(ApiConfig.Environment.DEV) }
    var heartbeatEnabled by remember { mutableStateOf(true) }
    var heartbeatVolume by remember { mutableFloatStateOf(15f) }

    LaunchedEffect(Unit) {
        prodUrl = prefs.getString("url_prod", "") ?: ""
        preprodUrl = prefs.getString("url_preprod", ApiConfig.defaultPreprodUrl) ?: ApiConfig.defaultPreprodUrl
        devUrl = prefs.getString("url_dev", ApiConfig.defaultDevUrl) ?: ApiConfig.defaultDevUrl
        apiKeyHeader = prefs.getString("api_key_header", ApiConfig.getApiKeyHeader(context))
            ?: ApiConfig.getApiKeyHeader(context)
        apiKey = prefs.getString("api_key", ApiConfig.getApiKey(context)) ?: ApiConfig.getApiKey(context)
        selectedEnv = ApiConfig.Environment.fromRaw(
            prefs.getString("env", ApiConfig.Environment.DEV.rawValue)
        )
        heartbeatEnabled = ServiceConfig.isHeartbeatEnabled(context)
        heartbeatVolume = ServiceConfig.getHeartbeatVolume(context).toFloat()
    }

    fun isValidUrl(value: String): Boolean {
        if (value.isBlank()) return false
        val uri = Uri.parse(value)
        val scheme = uri.scheme?.lowercase()
        return (scheme == "http" || scheme == "https") && !uri.host.isNullOrBlank()
    }

    val prodUrlError = prodUrl.isNotBlank() && !isValidUrl(prodUrl)
    val preprodUrlError = preprodUrl.isNotBlank() && !isValidUrl(preprodUrl)
    val devUrlError = devUrl.isNotBlank() && !isValidUrl(devUrl)
    val hasAnyError = prodUrlError || preprodUrlError || devUrlError
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuracion") },
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
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(
                value = prodUrl,
                onValueChange = { prodUrl = it },
                label = { Text("URL Produccion") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = prodUrlError,
                supportingText = { if (prodUrlError) Text("URL invalida") }
            )
            EnvironmentOption(
                text = "Usar Produccion",
                selected = selectedEnv == ApiConfig.Environment.PROD,
                onClick = { selectedEnv = ApiConfig.Environment.PROD }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = preprodUrl,
                onValueChange = { preprodUrl = it },
                label = { Text("URL Preproduccion") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = preprodUrlError,
                supportingText = { if (preprodUrlError) Text("URL invalida") }
            )
            EnvironmentOption(
                text = "Usar Preproduccion",
                selected = selectedEnv == ApiConfig.Environment.PREPROD,
                onClick = { selectedEnv = ApiConfig.Environment.PREPROD }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = devUrl,
                onValueChange = { devUrl = it },
                label = { Text("URL Desarrollo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = devUrlError,
                supportingText = { if (devUrlError) Text("URL invalida") }
            )
            EnvironmentOption(
                text = "Usar Desarrollo",
                selected = selectedEnv == ApiConfig.Environment.DEV,
                onClick = { selectedEnv = ApiConfig.Environment.DEV }
            )

            Spacer(Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = apiKeyHeader,
                onValueChange = { apiKeyHeader = it },
                label = { Text("Header API key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Bip en cada consulta", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = heartbeatEnabled,
                    onCheckedChange = { heartbeatEnabled = it }
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Volumen del bip de monitoreo: ${heartbeatVolume.toInt()}%",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Este deslizador controla cuan tenue o fuerte suena el bip que se reproduce en cada consulta.",
                style = MaterialTheme.typography.bodySmall
            )
            Slider(
                value = heartbeatVolume,
                onValueChange = { heartbeatVolume = it },
                valueRange = 0f..100f
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { BatteryOptimizationHelper.requestIgnoreBatteryOptimizations(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Excluir de ahorro de bateria")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { BatteryOptimizationHelper.openSamsungBatterySettings(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abrir ajustes Samsung")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { BatteryOptimizationHelper.openAppDetails(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abrir detalles de la app")
            }

            Spacer(Modifier.height(24.dp))

            val activeUrl = when (selectedEnv) {
                ApiConfig.Environment.PROD -> prodUrl
                ApiConfig.Environment.PREPROD -> preprodUrl
                ApiConfig.Environment.DEV -> devUrl
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
                        .putString("api_key_header", apiKeyHeader)
                        .putString("api_key", apiKey)
                        .putString("env", selectedEnv.rawValue)
                        .putString("base_url", activeUrl)
                        .commit()
                    ServiceConfig.setHeartbeatEnabled(context, heartbeatEnabled)
                    ServiceConfig.setHeartbeatVolume(context, heartbeatVolume.toInt())
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !hasAnyError
            ) {
                Text("Guardar configuracion")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EnvironmentOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text)
    }
}
