package com.example.mccagent.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mccagent.R
import com.example.mccagent.config.ServiceConfig
import com.example.mccagent.repository.ClientRepositoryImpl
import com.example.mccagent.repository.MessageRepositoryImpl
import com.example.mccagent.services.BatteryOptimizationHelper
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
    onMessages: () -> Unit,
    onRealTime: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ClientViewModel = viewModel(factory = ClientViewModelFactory(ClientRepositoryImpl(context)))

    var menuExpanded by remember { mutableStateOf(false) }
    val mostrarDialogoNumero = remember { mutableStateOf(false) }
    val estadoServicio = remember { mutableStateOf("INACTIVO") }
    val ultimaConsulta = remember { mutableStateOf("Sin consulta") }
    val pendientes = remember { mutableStateOf(0) }
    val errorSincronizacion = remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val state by viewModel.clientState.collectAsState()
    val currentDeviceId = remember { getCurrentDeviceId(context) }

    fun refrescarDatosLocales() {
        viewModel.loadClientInfo()
        scope.launch {
            try {
                pendientes.value = MessageRepositoryImpl(context).getPendingMessages().size
                errorSincronizacion.value = null
            } catch (e: Exception) {
                errorSincronizacion.value = "No se pudo consultar pendientes"
                Log.e("HomeScreen", "Error al refrescar pendientes", e)
            }
            refrescarEstado()
        }
    }

    fun refrescarEstado() {
        estadoServicio.value = if (ServiceConfig.isServiceEnabled(context)) "ACTIVO" else "INACTIVO"
        val ultima = ServiceConfig.getLastSyncEpochMs(context)
        ultimaConsulta.value = if (ultima > 0) {
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(ultima))
        } else {
            "Sin consulta"
        }
    }

    fun sincronizarAhora() {
        SmsWorkScheduler.schedule(context)
        SmsWorkScheduler.ejecutarSincronizacionInmediata(context)
        refrescarDatosLocales()
    }

    LaunchedEffect(Unit) {
        SmsWorkScheduler.schedule(context)
        refrescarDatosLocales()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_mccagent),
                            contentDescription = "Logo",
                            modifier = Modifier.size(28.dp)
                        )
                        Text("MCC SMS Agent")
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Home") },
                            onClick = { menuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Messages") },
                            onClick = {
                                menuExpanded = false
                                onMessages()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Real Time") },
                            onClick = {
                                menuExpanded = false
                                onRealTime()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                menuExpanded = false
                                onSettings()
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
                onClick = { BatteryOptimizationHelper.openSamsungBatterySettings(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ABRIR AJUSTES DE BATERIA", color = RojoCorporativo)
            }

            Text("Telefonos registrados", style = MaterialTheme.typography.titleMedium)
            PhonesList(dispositivos = state.devices, idActual = currentDeviceId)

            val deviceRegistered = state.devices.any { it.imei == currentDeviceId }
            if (!deviceRegistered) {
                OutlinedButton(
                    onClick = { mostrarDialogoNumero.value = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("REGISTRAR ESTE DISPOSITIVO", color = RojoCorporativo)
                }
            }

            val bateriaProtegida = BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)
            Text(
                text = if (bateriaProtegida) {
                    "Optimizacion de bateria: excluida"
                } else {
                    "Optimizacion de bateria: todavia activa"
                },
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
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
