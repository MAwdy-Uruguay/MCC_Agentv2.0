package com.example.mccagent.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = RojoCorporativo,
    onPrimary = FondoBlanco,
    secondary = TextoPrincipal,
    onSecondary = FondoBlanco,
    background = FondoBlanco,
    onBackground = TextoPrincipal,
    surface = FondoBlanco,
    onSurface = TextoPrincipal,
    error = RojoCorporativo,
    onError = FondoBlanco
)

private val EsquemaOscuro = darkColorScheme(
    primary = RojoCorporativo,
    onPrimary = FondoBlanco,
    background = TextoPrincipal,
    onBackground = FondoBlanco,
    surface = Color(0xFF1F272B),
    onSurface = FondoBlanco,
    error = RojoCorporativo,
    onError = FondoBlanco
)

@Composable
fun MCCAgentTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
