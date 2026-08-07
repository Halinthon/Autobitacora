package com.bitacora.vehicular.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AzulPrimario = Color(0xFF2F6FED)
val AzulOscuro = Color(0xFF1B4FC4)
val RojoAlerta = Color(0xFFE24B4A)
val AmbarAlerta = Color(0xFFBA7517)
val VerdeExito = Color(0xFF3B6D11)

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrimario,
    secondary = AzulOscuro,
    error = RojoAlerta
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulPrimario,
    secondary = AzulOscuro,
    error = RojoAlerta
)

@Composable
fun BitacoraVehicularTheme(content: @Composable () -> Unit) {
    val esquema = if (isSystemInDarkTheme()) EsquemaOscuro else EsquemaClaro
    MaterialTheme(colorScheme = esquema, content = content)
}
