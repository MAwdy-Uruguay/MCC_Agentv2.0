package com.example.mccagent.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mccagent.R
import com.example.mccagent.models.entities.Device
import com.example.mccagent.ui.theme.DivisorSuave
import com.example.mccagent.ui.theme.FondoActivoSuave
import com.example.mccagent.ui.theme.FondoTarjetaSuave
import com.example.mccagent.ui.theme.RojoCorporativo
import com.example.mccagent.ui.theme.TextoPrincipal

@Composable
fun CompanyCard(nombreEmpresa: String, subtitulo: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FondoTarjetaSuave),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Image(
                painter = painterResource(id = R.drawable.logo_mcc),
                contentDescription = "Logo corporativo",
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = nombreEmpresa.ifBlank { "MCC SMS Agent" },
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipal,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoPrincipal
            )
        }
    }
}

@Composable
fun PhonesList(dispositivos: List<Device>, idActual: String) {
    if (dispositivos.isEmpty()) {
        Text("No hay teléfonos registrados.", color = TextoPrincipal, style = MaterialTheme.typography.bodyMedium)
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        dispositivos.forEach { dispositivo ->
            val esActual = dispositivo.imei == idActual
            val fondo = if (esActual) FondoActivoSuave else FondoTarjetaSuave
            val borde = if (esActual) BorderStroke(1.dp, RojoCorporativo) else BorderStroke(1.dp, DivisorSuave)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = fondo),
                border = borde,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dispositivo.phone.ifBlank { "Sin teléfono" }, color = TextoPrincipal, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(dispositivo.name, color = TextoPrincipal, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (esActual) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(RojoCorporativo)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("ESTE DISPOSITIVO", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SystemFooterStatus(
    estadoServicio: String,
    pendientes: Int,
    ultimaConsulta: String,
    error: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = FondoTarjetaSuave,
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        tonalElevation = 0.dp,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Estado del sistema", style = MaterialTheme.typography.titleMedium, color = TextoPrincipal)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Servicio: $estadoServicio", color = TextoPrincipal, style = MaterialTheme.typography.bodyMedium)
            Text("Pendientes: $pendientes", color = TextoPrincipal, style = MaterialTheme.typography.bodyMedium)
            Text("Última consulta: $ultimaConsulta", color = TextoPrincipal, style = MaterialTheme.typography.bodyMedium)
            if (!error.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = DivisorSuave)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Error: $error", color = TextoPrincipal, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
