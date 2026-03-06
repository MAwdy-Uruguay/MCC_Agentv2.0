package com.example.mccagent.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mccagent.R
import com.example.mccagent.models.entities.Message
import com.example.mccagent.repository.MessageRepositoryImpl
import com.example.mccagent.viewmodels.RealTimeMessagesViewModel
import com.example.mccagent.viewmodels.RealTimeMessagesViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealTimeMessagesScreen(
    onHome: () -> Unit,
    onMessages: () -> Unit,
    onSettings: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: RealTimeMessagesViewModel = viewModel(
        factory = RealTimeMessagesViewModelFactory(MessageRepositoryImpl(context))
    )
    val uiState by viewModel.uiState.collectAsState()

    var menuExpanded by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        viewModel.startMonitoring()
        onDispose {
            viewModel.stopMonitoring()
        }
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
                        Text("Real Time")
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Home") },
                            onClick = {
                                menuExpanded = false
                                onHome()
                            }
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
                            onClick = { menuExpanded = false }
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Monitoreo de pendientes (actualiza cada 3s)",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ultima actualizacion: ${uiState.lastUpdate}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.loading && uiState.messages.isEmpty()) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!uiState.error.isNullOrBlank()) {
                Text(
                    text = uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    RealTimeMessageItem(message)
                }
            }
        }
    }
}

@Composable
private fun RealTimeMessageItem(message: Message) {
    val normalizedStatus = message.status.trim().uppercase()
    val backgroundColor = statusColor(normalizedStatus)
    val textColor = if (normalizedStatus == "ENVIANDO") Color.Black else Color.White

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Subject: ${message.subject}", style = MaterialTheme.typography.titleSmall)
        Text("Recipient: ${message.recipient}", style = MaterialTheme.typography.bodyMedium)

        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .background(backgroundColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("${message.status}", color = textColor, style = MaterialTheme.typography.labelMedium)
        }

        Text("Fecha: ${message.createdAt ?: "-"}", style = MaterialTheme.typography.bodySmall)
    }
}

private fun statusColor(status: String): Color {
    return when (status) {
        "PENDIENTE" -> Color(0xFF8E24AA)
        "ENVIANDO" -> Color(0xFFFDD835)
        "ENVIADO" -> Color(0xFF2E7D32)
        "FALLIDO" -> Color(0xFFC62828)
        "CANCELADO" -> Color(0xFF000000)
        else -> Color(0xFF616161)
    }
}
