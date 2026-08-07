package com.bitacora.vehicular.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class OpcionMas(val titulo: String, val icono: androidx.compose.ui.graphics.vector.ImageVector, val ruta: String)

val opcionesMas = listOf(
    OpcionMas("Incidentes", Icons.Default.ReportProblem, "incidentes"),
    OpcionMas("Impuestos", Icons.Default.Receipt, "impuestos"),
    OpcionMas("Otros pagos", Icons.Default.Payments, "otros_pagos"),
    OpcionMas("Kilometraje", Icons.Default.Speed, "kilometraje"),
    OpcionMas("Combustible", Icons.Default.LocalGasStation, "combustible"),
    OpcionMas("Enlaces de interés", Icons.Default.Link, "enlaces"),
    OpcionMas("Respaldo de datos", Icons.Default.Backup, "backup")
)

@Composable
fun MasScreen(alSeleccionar: (String) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(opcionesMas) { opcion ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .irA(opcion.ruta, alSeleccionar)
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(opcion.icono, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(opcion.titulo, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

private fun Modifier.irA(ruta: String, alSeleccionar: (String) -> Unit): Modifier =
    this.then(Modifier.clickable { alSeleccionar(ruta) })
