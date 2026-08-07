package com.bitacora.vehicular.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bitacora.vehicular.ui.theme.PaletaFondos
import com.bitacora.vehicular.util.TemaPreferencias

private const val RUTA_ACERCA_DE = "acerca_de"
private const val RUTA_APARIENCIA = "apariencia"

data class OpcionMas(val titulo: String, val icono: androidx.compose.ui.graphics.vector.ImageVector, val ruta: String)

val opcionesMas = listOf(
    OpcionMas("Tarjeta de propiedad", Icons.Default.Badge, "tarjeta_propiedad"),
    OpcionMas("Licencia de conducción", Icons.Default.CreditCard, "licencia_conduccion"),
    OpcionMas("Incidentes", Icons.Default.ReportProblem, "incidentes"),
    OpcionMas("Impuestos", Icons.Default.Receipt, "impuestos"),
    OpcionMas("Otros pagos", Icons.Default.Payments, "otros_pagos"),
    OpcionMas("Kilometraje", Icons.Default.Speed, "kilometraje"),
    OpcionMas("Combustible", Icons.Default.LocalGasStation, "combustible"),
    OpcionMas("Enlaces de interés", Icons.Default.Link, "enlaces"),
    OpcionMas("Respaldo de datos", Icons.Default.Backup, "backup"),
    OpcionMas("Apariencia", Icons.Default.Palette, RUTA_APARIENCIA),
    OpcionMas("Acerca de", Icons.Default.Info, RUTA_ACERCA_DE)
)

@Composable
fun MasScreen(alSeleccionar: (String) -> Unit) {
    var mostrarAcercaDe by remember { mutableStateOf(false) }
    var mostrarApariencia by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(opcionesMas) { opcion ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .irA(opcion.ruta) { ruta ->
                        when (ruta) {
                            RUTA_ACERCA_DE -> mostrarAcercaDe = true
                            RUTA_APARIENCIA -> mostrarApariencia = true
                            else -> alSeleccionar(ruta)
                        }
                    }
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(opcion.icono, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(opcion.titulo, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    if (mostrarAcercaDe) {
        DialogoAcercaDe { mostrarAcercaDe = false }
    }
    if (mostrarApariencia) {
        DialogoApariencia { mostrarApariencia = false }
    }
}

@Composable
private fun DialogoAcercaDe(alCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text("Acerca de") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Diseño y desarrollo by Halinthon")
                Text("halinthon@gmail.com")
                Text("Aug. 2026")
            }
        },
        confirmButton = { TextButton(onClick = alCerrar) { Text("Cerrar") } }
    )
}

@Composable
private fun DialogoApariencia(alCerrar: () -> Unit) {
    val context = LocalContext.current
    val colorActual = TemaPreferencias.colorFondo

    Dialog(onDismissRequest = alCerrar) {
        BoxWithConstraints {
            val alturaMaxima = maxHeight * 0.85f
            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().heightIn(max = alturaMaxima)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Color de fondo", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Elige el color de fondo de la aplicación.", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        PaletaFondos.forEach { (nombre, color) ->
                            val seleccionado = colorActual == color
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { TemaPreferencias.establecer(context, color) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(color ?: MaterialTheme.colorScheme.surfaceVariant)
                                        .then(
                                            if (seleccionado)
                                                Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                            else Modifier
                                        )
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(nombre, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1f))
                                if (seleccionado) Icon(Icons.Default.Check, contentDescription = "Seleccionado")
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = alCerrar) { Text("Cerrar") }
                    }
                }
            }
        }
    }
}

private fun Modifier.irA(ruta: String, alSeleccionar: (String) -> Unit): Modifier =
    this.then(Modifier.clickable { alSeleccionar(ruta) })
