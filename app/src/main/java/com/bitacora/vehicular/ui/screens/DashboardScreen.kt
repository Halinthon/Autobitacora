@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bitacora.vehicular.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bitacora.vehicular.data.RegistroOdometro
import com.bitacora.vehicular.ui.components.CampoFecha
import com.bitacora.vehicular.ui.components.CampoTexto
import com.bitacora.vehicular.ui.components.DialogoFormulario
import com.bitacora.vehicular.ui.components.TarjetaAlerta
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun DashboardScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    val alertas by viewModel.alertas.collectAsState()
    val resumen by viewModel.resumenGastos.collectAsState()
    val rango by viewModel.rangoFechas.collectAsState()
    val ultimoKm by viewModel.ultimoKilometraje.collectAsState()
    var mostrarDialogoKm by remember { mutableStateOf(false) }
    var mostrarDialogoFiltro by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            if (vehiculo == null) {
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Registra tu primer vehículo en la sección Vehículo para comenzar.")
                }
            } else {
                val v = vehiculo!!
                Box(Modifier.fillMaxWidth().height(140.dp)) {
                    if (v.fotoPath != null) {
                        AsyncImage(
                            model = java.io.File(v.fotoPath), contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))
                    } else {
                        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer))
                    }
                    Row(
                        Modifier.fillMaxSize().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (v.fotoPath == null) Icon(Icons.Default.DirectionsCar, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            val colorTexto = if (v.fotoPath != null) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                            Text("${v.marca} · ${v.modeloAnio}", style = MaterialTheme.typography.titleMedium, color = colorTexto)
                            Text("Placa ${v.placa}", color = colorTexto)
                        }
                        FilledTonalButton(onClick = { mostrarDialogoKm = true }) {
                            Icon(Icons.Default.Speed, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Registrar km")
                        }
                    }
                }
            }
        }

        if (vehiculo != null) {
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Último kilometraje: ${ultimoKm?.let { "${it.kilometraje} km (${it.fecha.format(formatoFecha)})" } ?: "Sin registros"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (alertas.isNotEmpty()) {
            item { Text("Alertas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp)) }
            items(alertas) { alerta -> Box(Modifier.padding(horizontal = 16.dp)) { TarjetaAlerta(alerta.titulo, alerta.detalle, alerta.esUrgente) } }
        }

        item {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Resumen de gastos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = { mostrarDialogoFiltro = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (rango == null) "Filtrar" else "Filtrado")
                }
            }
        }
        if (rango != null) {
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Del ${rango!!.desde.format(formatoFecha)} al ${rango!!.hasta.format(formatoFecha)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { viewModel.limpiarRangoFechas() }) { Text("Ver todo") }
                }
            }
        }
        item {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                FilaGasto("Cambios de aceite", resumen.aceite)
                FilaGasto("Reparaciones", resumen.reparaciones)
                FilaGasto("Autopartes", resumen.autopartes)
                FilaGasto("Documentos (SOAT/Tecno.)", resumen.documentos)
                FilaGasto("Impuestos", resumen.impuestos)
                FilaGasto("Combustible", resumen.combustible)
                FilaGasto("Otros pagos", resumen.otros)
                Divider(Modifier.padding(vertical = 6.dp))
                FilaGasto("Total", resumen.total, negrita = true)
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (mostrarDialogoKm && vehiculo != null) {
        var km by remember { mutableStateOf("") }
        DialogoFormulario(
            titulo = "Registrar kilometraje actual",
            alConfirmar = {
                val valor = km.toIntOrNull() ?: return@DialogoFormulario
                viewModel.agregarRegistroOdometro(RegistroOdometro(vehiculoId = vehiculo!!.id, fecha = LocalDate.now(), kilometraje = valor))
                mostrarDialogoKm = false
            },
            alCancelar = { mostrarDialogoKm = false }
        ) {
            CampoTexto("Kilometraje", km, numerico = true) { km = it.filter { c -> c.isDigit() } }
        }
    }

    if (mostrarDialogoFiltro) {
        var desde by remember { mutableStateOf(rango?.desde ?: LocalDate.now().minusMonths(1)) }
        var hasta by remember { mutableStateOf(rango?.hasta ?: LocalDate.now()) }
        DialogoFormulario(
            titulo = "Filtrar por rango de fechas",
            alConfirmar = {
                viewModel.aplicarRangoFechas(desde, hasta)
                mostrarDialogoFiltro = false
            },
            alCancelar = { mostrarDialogoFiltro = false }
        ) {
            CampoFecha("Desde", desde) { desde = it }
            CampoFecha("Hasta", hasta) { hasta = it }
        }
    }
}

@Composable
private fun FilaGasto(etiqueta: String, valor: Double, negrita: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal)
        Text("$${"%,.0f".format(valor)}", fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal)
    }
}
