package com.example.mccagent.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mccagent.config.ApiConfig

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
                .padding(24.dp)
                .fillMaxSize(),
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
                supportingText = {
                    if (prodUrlError) {
                        Text("URL invalida")
                    }
                }
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
                supportingText = {
                    if (preprodUrlError) {
                        Text("URL invalida")
                    }
                }
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
                supportingText = {
                    if (devUrlError) {
                        Text("URL invalida")
                    }
                }
            )
            EnvironmentOption(
                text = "Usar Desarrollo",
                selected = selectedEnv == ApiConfig.Environment.DEV,
                onClick = { selectedEnv = ApiConfig.Environment.DEV }
            )

            Spacer(Modifier.height(24.dp))

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
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !hasAnyError
            ) {
                Text("Guardar configuracion")
            }
        }
    }
}

@Composable
private fun EnvironmentOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text)
    }
}
