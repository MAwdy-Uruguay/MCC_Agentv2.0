package com.example.mccagent.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mccagent.config.ApiConfig
import com.example.mccagent.data.LoginRequest
import com.example.mccagent.network.RetrofitClient
import com.example.mccagent.repository.AuthRepositoryImpl
import com.example.mccagent.ui.theme.RojoCorporativo
import com.example.mccagent.utils.SecureSessionStorage
import com.example.mccagent.viewmodels.AuthState
import com.example.mccagent.viewmodels.AuthViewModel
import com.example.mccagent.viewmodels.AuthViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isProd by remember { mutableStateOf(false) }
    var currentEnv by remember { mutableStateOf(ApiConfig.Environment.DEV) }
    var baseUrl by remember { mutableStateOf("") }

    fun recargarConfiguracion() {
        val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
        isProd = prefs.getString("env", "DEV") == "PROD"
        currentEnv = ApiConfig.getEnv(context)
        baseUrl = ApiConfig.getBaseUrl(context)
    }

    LaunchedEffect(Unit) {
        recargarConfiguracion()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                recargarConfiguracion()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val viewModel: AuthViewModel = viewModel(
        key = "auth_${currentEnv.rawValue}_$baseUrl",
        factory = AuthViewModelFactory(AuthRepositoryImpl(RetrofitClient.getApiService(context)))
    )

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                val response = authState as AuthState.Success
                val token = response.response.token
                SecureSessionStorage.guardarToken(context, token)
                scope.launch {
                    snackbarHostState.showSnackbar("✅ Bienvenido, ${response.response.user.fullname}")
                }
                viewModel.resetState()
                onLoginSuccess()
            }

            is AuthState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar("❌ ${(authState as AuthState.Error).message}")
                }
                viewModel.resetState()
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenSettings,
                containerColor = RojoCorporativo,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Configuración")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Email / Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modo Producción")
                Switch(
                    checked = isProd,
                    onCheckedChange = {
                        isProd = it
                        val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
                        prefs.edit().putString("env", if (isProd) "PROD" else "DEV").commit()
                        recargarConfiguracion()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Entorno: ${currentEnv.rawValue}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Base URL: $baseUrl",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        try {
                            val urlActiva = ApiConfig.getBaseUrl(context).trim()
                            if (urlActiva.isBlank()) {
                                snackbarHostState.showSnackbar("❌ URL de servicio vacía. Revisá Configuración.")
                                return@launch
                            }

                            val endpoint = construirEndpointCheckService(urlActiva)
                            val client = OkHttpClient.Builder()
                                .connectTimeout(5, TimeUnit.SECONDS)
                                .readTimeout(5, TimeUnit.SECONDS)
                                .build()

                            val request = Request.Builder().url(endpoint).get().build()
                            client.newCall(request).execute().use { response ->
                                val body = response.body?.string().orEmpty()
                                val okJson = runCatching { JSONObject(body).optBoolean("ok", false) }.getOrDefault(false)
                                val message = when {
                                    !response.isSuccessful -> "⚠️ Servicio alcanzable pero con código ${response.code}"
                                    okJson -> "✅ Servicio disponible (checkservice ok=true)"
                                    else -> "❌ Servicio respondió ok=false"
                                }
                                snackbarHostState.showSnackbar(message)
                            }
                        } catch (e: Exception) {
                            val detalle = e.localizedMessage?.takeIf { it.isNotBlank() }
                                ?: "No se pudo establecer conexión con la URL configurada"
                            snackbarHostState.showSnackbar("❌ Error de conexión: $detalle")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Probar conexión")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.login(LoginRequest(username.trim(), password))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }

            if (authState is AuthState.Loading) {
                Spacer(modifier = Modifier.height(20.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

private fun construirEndpointCheckService(baseUrl: String): String {
    val normalizada = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
    return if (normalizada.contains("/api/")) {
        "${normalizada}auth/checkservice"
    } else {
        "${normalizada}api/auth/checkservice"
    }
}
