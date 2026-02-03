package com.example.mccagent.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mccagent.utils.registrarEsteDispositivo

@Composable
fun DialogRegistrarTelefono(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var numero by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun isValidPhone(input: String): Boolean {
        val cleaned = input.replace(" ", "")
        return cleaned.matches(Regex("^\\+?\\d{6,15}$"))
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Registrar dispositivo") },
        text = {
            Column {
                Text("Ingresa tu número de teléfono para registrar este dispositivo.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = numero,
                    onValueChange = { numero = it },
                    label = { Text("Teléfono") },
                    isError = error != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (!isValidPhone(numero)) {
                    error = "Debe ingresar un número válido (6 a 15 dígitos)"
                } else {
                    registrarEsteDispositivo(
                        context = context,
                        numero = numero.replace(" ", ""),
                        onSuccess = onSuccess,
                        onError = {
                            error = it
                        }
                    )
                }
            }) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}
