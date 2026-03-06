package com.example.mccagent.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mccagent.R
import com.example.mccagent.models.entities.Message
import com.example.mccagent.repository.MessageRepositoryImpl
import com.example.mccagent.viewmodels.MessageFilter
import com.example.mccagent.viewmodels.MessagesViewModel
import com.example.mccagent.viewmodels.MessagesViewModelFactory
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onHome: () -> Unit,
    onSettings: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: MessagesViewModel = viewModel(
        factory = MessagesViewModelFactory(MessageRepositoryImpl(context))
    )
    val uiState by viewModel.uiState.collectAsState()

    val listState = rememberLazyListState()
    var menuExpanded by remember { mutableStateOf(false) }
    var filterExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(listState, uiState.messages.size, uiState.loading) {
        snapshotFlow {
            val total = listState.layoutInfo.totalItemsCount
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && lastVisible >= total - 3
        }
            .distinctUntilChanged()
            .collect { nearEnd ->
                if (nearEnd && !uiState.loading) {
                    viewModel.loadMore()
                }
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
                        Text("Mensajes")
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
            OutlinedButton(onClick = { filterExpanded = true }) {
                Text("Filtro: ${filterLabel(uiState.filter)}")
            }
            DropdownMenu(expanded = filterExpanded, onDismissRequest = { filterExpanded = false }) {
                DropdownMenuItem(text = { Text("Pendiente") }, onClick = {
                    filterExpanded = false
                    viewModel.updateFilter(MessageFilter.PENDING)
                })
                DropdownMenuItem(text = { Text("Enviado") }, onClick = {
                    filterExpanded = false
                    viewModel.updateFilter(MessageFilter.SENT)
                })
                DropdownMenuItem(text = { Text("Fallido") }, onClick = {
                    filterExpanded = false
                    viewModel.updateFilter(MessageFilter.FAILED)
                })
                DropdownMenuItem(text = { Text("Todos") }, onClick = {
                    filterExpanded = false
                    viewModel.updateFilter(MessageFilter.ALL)
                })
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.loading && uiState.messages.isEmpty()) {
                CircularProgressIndicator()
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
                state = listState,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    MessageItem(message = message)
                }

                if (uiState.loading && uiState.messages.isNotEmpty()) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun filterLabel(filter: MessageFilter): String {
    return when (filter) {
        MessageFilter.PENDING -> "Pendiente"
        MessageFilter.SENT -> "Enviado"
        MessageFilter.FAILED -> "Fallido"
        MessageFilter.ALL -> "Todos"
    }
}

@Composable
private fun MessageItem(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Subject: ${message.subject}", style = MaterialTheme.typography.titleSmall)
        Text("Recipient: ${message.recipient}", style = MaterialTheme.typography.bodyMedium)
        Text("Body: ${message.body}", style = MaterialTheme.typography.bodyMedium)
        Text("Status: ${message.status}", style = MaterialTheme.typography.bodyMedium)
    }
}
