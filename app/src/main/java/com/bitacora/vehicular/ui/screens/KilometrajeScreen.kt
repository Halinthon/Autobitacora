package com.bitacora.vehicular.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitacora.vehicular.data.RegistroOdometro
import com.bitacora.vehicular.ui.components.CampoFecha
import com.bitacora.vehicular.ui.components.CampoTexto
import com.bitacora.vehicular.ui.components.DialogoFormulario
import com.bitacora.vehicular.ui.components.TarjetaRegistro
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import java.time.LocalDate

@Composable
fun KilometrajeScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    val lista by viewModel.registrosOdometro.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    if (vehiculo == null) { SinVehiculoAviso(); return }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar") }
        }
    ) { padding ->
        if (lista.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Aún no has registrado kilometraje para este vehículo.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
                items(lista, key = { it.id }) { r ->
                    TarjetaRegistro(
                        titulo = "${r.kilometraje} km",
                        subtitulo = "${r.fecha}",
                        alEliminar = { viewModel.eliminarRegistroOdometro(r) }
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        var fecha by remember { mutableStateOf(LocalDate.now()) }
        var km by remember { mutableStateOf("") }

        DialogoFormulario(
            titulo = "Registrar kilometraje",
            alConfirmar = {
                val valor = km.toIntOrNull() ?: return@DialogoFormulario
                viewModel.agregarRegistroOdometro(RegistroOdometro(vehiculoId = vehiculo!!.id, fecha = fecha, kilometraje = valor))
                mostrarDialogo = false
            },
            alCancelar = { mostrarDialogo = false }
        ) {
            CampoFecha("Fecha", fecha) { fecha = it }
            CampoTexto("Kilometraje", km, numerico = true) { km = it.filter { c -> c.isDigit() } }
        }
    }
}
