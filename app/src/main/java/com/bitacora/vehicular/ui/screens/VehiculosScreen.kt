package com.bitacora.vehicular.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitacora.vehicular.data.TipoVehiculo
import com.bitacora.vehicular.data.Vehiculo
import com.bitacora.vehicular.ui.components.CampoSeleccion
import com.bitacora.vehicular.ui.components.CampoTexto
import com.bitacora.vehicular.ui.components.DialogoFormulario
import com.bitacora.vehicular.ui.components.TarjetaRegistro
import com.bitacora.vehicular.viewmodel.BitacoraViewModel

@Composable
fun VehiculosScreen(viewModel: BitacoraViewModel) {
    val vehiculos by viewModel.vehiculos.collectAsState()
    val seleccionadoId by viewModel.vehiculoSeleccionadoId.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar vehículo") }
        }
    ) { padding ->
        if (vehiculos.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Aún no has registrado ningún vehículo.\nToca + para agregar el primero.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
                items(vehiculos, key = { it.id }) { v ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = if (v.id == seleccionadoId) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors()
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp).clickableSeleccionar { viewModel.seleccionarVehiculo(v.id) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text("${v.marca} · ${v.modeloAnio}", style = MaterialTheme.typography.titleMedium)
                                Text("${v.tipo.name.lowercase().replaceFirstChar { it.uppercase() }} · Placa ${v.placa}")
                                Text("Matriculado en ${v.lugarMatricula}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { viewModel.eliminarVehiculo(v) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        var tipo by remember { mutableStateOf(TipoVehiculo.AUTO) }
        var placa by remember { mutableStateOf("") }
        var marca by remember { mutableStateOf("") }
        var modeloAnio by remember { mutableStateOf("") }
        var lugar by remember { mutableStateOf("") }

        DialogoFormulario(
            titulo = "Nuevo vehículo",
            alConfirmar = {
                val anio = modeloAnio.toIntOrNull() ?: return@DialogoFormulario
                if (placa.isBlank() || marca.isBlank() || lugar.isBlank()) return@DialogoFormulario
                viewModel.agregarVehiculo(Vehiculo(tipo = tipo, placa = placa.uppercase(), marca = marca, modeloAnio = anio, lugarMatricula = lugar))
                mostrarDialogo = false
            },
            alCancelar = { mostrarDialogo = false }
        ) {
            CampoSeleccion("Tipo", TipoVehiculo.entries, tipo, { if (it == TipoVehiculo.MOTO) "Moto" else "Auto" }) { tipo = it }
            CampoTexto("Placa", placa) { placa = it }
            CampoTexto("Marca", marca) { marca = it }
            CampoTexto("Modelo (año)", modeloAnio, { modeloAnio = it.filter { c -> c.isDigit() } }, numerico = true)
            CampoTexto("Lugar de matrícula (ciudad)", lugar) { lugar = it }
        }
    }
}

private fun Modifier.clickableSeleccionar(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(onClick = onClick)
)
