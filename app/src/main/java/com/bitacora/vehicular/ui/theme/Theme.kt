package com.bitacora.vehicular.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.bitacora.vehicular.util.TemaPreferencias

val AzulPrimario = Color(0xFF2F6FED)
val AzulOscuro = Color(0xFF1B4FC4)
val RojoAlerta = Color(0xFFE24B4A)
val AmbarAlerta = Color(0xFFBA7517)
val VerdeExito = Color(0xFF3B6D11)

/** Paleta de colores de fondo que el usuario puede elegir desde "Más → Apariencia". */
val PaletaFondos = listOf(
    "Predeterminado" to null,
    "Azul claro" to Color(0xFFE6F1FB),
    "Verde claro" to Color(0xFFEAF3DE),
    "Amarillo claro" to Color(0xFFFAEEDA),
    "Rosa claro" to Color(0xFFFBEAF0),
    "Morado claro" to Color(0xFFEEEDFE),
    "Gris claro" to Color(0xFFF1EFE8)
)

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
    val base = if (isSystemInDarkTheme()) EsquemaOscuro else EsquemaClaro
    val fondoElegido = TemaPreferencias.colorFondo
    val esquema = if (fondoElegido != null) {
        base.copy(background = fondoElegido, surface = fondoElegido)
    } else {
        base
    }
    MaterialTheme(colorScheme = esquema, content = content)
}
