package com.bitacora.vehicular.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitacora.vehicular.data.RegistroOdometro
import com.bitacora.vehicular.ui.components.CampoTexto
import com.bitacora.vehicular.ui.components.DialogoFormulario
import com.bitacora.vehicular.ui.components.TarjetaAlerta
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import java.time.LocalDate

@Composable
fun DashboardScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    val alertas by viewModel.alertas.collectAsState()
    val resumen by viewModel.resumenGastos.collectAsState()
    var mostrarDialogoKm by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            if (vehiculo == null) {
                Text("Registra tu primer vehículo en la sección Vehículo para comenzar.")
            } else {
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("${vehiculo!!.marca} · ${vehiculo!!.modeloAnio}", style = MaterialTheme.typography.titleMedium)
                            Text("Placa ${vehiculo!!.placa}")
                        }
                        TextButton(onClick = { mostrarDialogoKm = true }) {
                            Icon(Icons.Default.Speed, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Registrar km")
                        }
                    }
                }
            }
        }

        if (alertas.isNotEmpty()) {
            item { Text("Alertas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            items(alertas) { alerta -> TarjetaAlerta(alerta.titulo, alerta.detalle, alerta.esUrgente) }
        }

        item { Text("Resumen de gastos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Column {
                FilaGasto("Cambios de aceite", resumen.aceite)
                FilaGasto("Reparaciones", resumen.reparaciones)
                FilaGasto("Autopartes", resumen.autopartes)
                FilaGasto("Documentos (SOAT/Tecno.)", resumen.documentos)
                FilaGasto("Impuestos", resumen.impuestos)
                FilaGasto("Otros pagos", resumen.otros)
                Divider(Modifier.padding(vertical = 6.dp))
                FilaGasto("Total", resumen.total, negrita = true)
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
}

@Composable
private fun FilaGasto(etiqueta: String, valor: Double, negrita: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal)
        Text("$${"%,.0f".format(valor)}", fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal)
    }
}
