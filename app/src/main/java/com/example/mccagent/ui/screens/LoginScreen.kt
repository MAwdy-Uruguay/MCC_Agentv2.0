package com.example.mccagent.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mccagent.viewmodels.*
import com.example.mccagent.repository.AuthRepositoryImpl
import com.example.mccagent.network.RetrofitClient
import com.example.mccagent.data.LoginRequest
import com.example.mccagent.config.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onOpenSettings: () -> Unit = {} // ← Callback para ir a Configuración
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepositoryImpl(RetrofitClient.getApiService(context)))
    )
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isProd by remember { mutableStateOf(false) }
    var currentEnv by remember { mutableStateOf(ApiConfig.Environment.DEV) }
    var baseUrl by remember { mutableStateOf("") }

    // 🔄 Cargar preferencia de entorno
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
        isProd = prefs.getString("env", "DEV") == "PROD"
        currentEnv = ApiConfig.getEnv(context)
        baseUrl = ApiConfig.getBaseUrl(context)
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                val response = authState as AuthState.Success
                val token = response.response.token

                val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("token", token).apply()

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
            FloatingActionButton(onClick = onOpenSettings) {
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
                        prefs.edit().putString("env", if (isProd) "PROD" else "DEV").apply()
                        currentEnv = ApiConfig.getEnv(context)
                        baseUrl = ApiConfig.getBaseUrl(context)
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
                            val client = OkHttpClient.Builder()
                                .connectTimeout(5, TimeUnit.SECONDS)
                                .readTimeout(5, TimeUnit.SECONDS)
                                .build()
                            val request = Request.Builder().url(baseUrl).get().build()
                            client.newCall(request).execute().use { response ->
                                val message = if (response.isSuccessful) {
                                    "✅ Conexión OK (${response.code})"
                                } else {
                                    "⚠️ Respuesta ${response.code}"
                                }
                                snackbarHostState.showSnackbar(message)
                            }
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("❌ Error de conexión: ${e.message}")
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
