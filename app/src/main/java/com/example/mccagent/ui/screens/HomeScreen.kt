package com.example.mccagent.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mccagent.repository.ClientRepositoryImpl
import com.example.mccagent.repository.MessageRepositoryImpl
import com.example.mccagent.ui.components.CompanyCard
import com.example.mccagent.ui.components.DialogRegistrarTelefono
import com.example.mccagent.ui.components.PhonesList
import com.example.mccagent.ui.components.SystemFooterStatus
import com.example.mccagent.ui.theme.FondoBlanco
import com.example.mccagent.ui.theme.RojoCorporativo
import com.example.mccagent.viewmodels.ClientViewModel
import com.example.mccagent.viewmodels.ClientViewModelFactory
import com.example.mccagent.workers.SmsWorkScheduler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ClientViewModel = viewModel(factory = ClientViewModelFactory(ClientRepositoryImpl(context)))

    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val mostrarDialogoNumero = remember { mutableStateOf(false) }

    val estadoServicio = remember { mutableStateOf("INACTIVO") }
    val ultimaConsulta = remember { mutableStateOf("Sin consulta") }
    val pendientes = remember { mutableStateOf(0) }
    val errorSincronizacion = remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val state by viewModel.clientState.collectAsState()
    val currentDeviceId = remember { getCurrentDeviceId(context) }

    fun sincronizarAhora() {
        val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
        estadoServicio.value = if (prefs.getBoolean("sms_service_running", false)) "ACTIVO" else "INACTIVO"
        viewModel.loadClientInfo()
        SmsWorkScheduler.ejecutarSincronizacionInmediata(context)

        scope.launch {
            try {
                pendientes.value = MessageRepositoryImpl(context).getPendingMessages().size
                errorSincronizacion.value = null
            } catch (e: Exception) {
                errorSincronizacion.value = "No se pudo consultar pendientes"
                Log.e("HomeScreen", "Error al refrescar pendientes", e)
            }

            val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            ultimaConsulta.value = formatter.format(Date())
        }
    }

    LaunchedEffect(Unit) {
        sincronizarAhora()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MCC SMS Agent") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Configuración") },
                            onClick = {
                                menuExpanded = false
                                onSettings()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                menuExpanded = false
                                showLogoutDialog = true
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            SystemFooterStatus(
                estadoServicio = if (state.error != null) "ERROR" else estadoServicio.value,
                pendientes = pendientes.value,
                ultimaConsulta = ultimaConsulta.value,
                error = state.error ?: errorSincronizacion.value
            )
        },
        containerColor = FondoBlanco
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoBlanco)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            CompanyCard(
                nombreEmpresa = state.clientName,
                subtitulo = "SMS Payment Link Gateway"
            )

            Button(
                onClick = { sincronizarAhora() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoCorporativo)
            ) {
                Text("SINCRONIZAR AHORA", color = MaterialTheme.colorScheme.onPrimary)
            }

            OutlinedButton(
                onClick = onSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("CONFIGURACIÓN", color = RojoCorporativo)
            }

            Text("Teléfonos registrados", style = MaterialTheme.typography.titleMedium)
            PhonesList(dispositivos = state.devices, idActual = currentDeviceId)

            val deviceRegistered = state.devices.any { it.imei == currentDeviceId }
            if (!deviceRegistered) {
                OutlinedButton(onClick = { mostrarDialogoNumero.value = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("REGISTRAR ESTE DISPOSITIVO", color = RojoCorporativo)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("¿Cerrar sesión?") },
                text = { Text("¿Deseas cerrar sesión y detener el servicio de mensajería?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }) {
                        Text("Sí, cerrar", color = RojoCorporativo)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (mostrarDialogoNumero.value) {
            DialogRegistrarTelefono(
                onDismiss = { mostrarDialogoNumero.value = false },
                onSuccess = {
                    mostrarDialogoNumero.value = false
                    sincronizarAhora()
                }
            )
        }
    }
}

@SuppressLint("HardwareIds")
fun getCurrentDeviceId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}
