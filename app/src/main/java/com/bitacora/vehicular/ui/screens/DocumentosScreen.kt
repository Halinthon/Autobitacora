package com.bitacora.vehicular.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitacora.vehicular.data.Soat
import com.bitacora.vehicular.data.Tecnomecanica
import com.bitacora.vehicular.ui.components.*
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import java.time.LocalDate

@Composable
fun DocumentosScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    var tab by remember { mutableStateOf(0) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    if (vehiculo == null) { SinVehiculoAviso(); return }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar") }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Tecnomecánica") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("SOAT") })
            }
            when (tab) {
                0 -> ListaTecnomecanica(viewModel)
                1 -> ListaSoat(viewModel)
            }
        }
    }

    if (mostrarDialogo) {
        when (tab) {
            0 -> DialogoTecnomecanica(viewModel, vehiculo!!.id) { mostrarDialogo = false }
            1 -> DialogoSoat(viewModel, vehiculo!!.id) { mostrarDialogo = false }
        }
    }
}

@Composable
private fun ListaTecnomecanica(viewModel: BitacoraViewModel) {
    val lista by viewModel.tecnomecanicas.collectAsState()
    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(lista, key = { it.id }) { t ->
            TarjetaRegistro(
                titulo = "Vence: ${t.fechaVencimiento}",
                subtitulo = "Expedida: ${t.fechaExpedicion} · ${t.lugar}",
                detalle = "$${"%,.0f".format(t.valor)}",
                fotoPath = t.fotoPath,
                alEliminar = { viewModel.eliminarTecnomecanica(t) }
            )
        }
    }
}

@Composable
private fun ListaSoat(viewModel: BitacoraViewModel) {
    val lista by viewModel.soats.collectAsState()
    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(lista, key = { it.id }) { s ->
            TarjetaRegistro(
                titulo = "Vence: ${s.fechaVencimiento}",
                subtitulo = "Expedido: ${s.fechaExpedicion} · ${s.lugar}",
                detalle = "$${"%,.0f".format(s.valor)}",
                fotoPath = s.fotoPath,
                alEliminar = { viewModel.eliminarSoat(s) }
            )
        }
    }
}

@Composable
private fun DialogoTecnomecanica(viewModel: BitacoraViewModel, vehiculoId: Long, alCerrar: () -> Unit) {
    var fechaExp by remember { mutableStateOf(LocalDate.now()) }
    var valor by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf<String?>(null) }

    DialogoFormulario(
        titulo = "Nueva tecnomecánica",
        alConfirmar = {
            val valorNum = valor.toDoubleOrNull() ?: return@DialogoFormulario
            if (lugar.isBlank()) return@DialogoFormulario
            viewModel.agregarTecnomecanica(
                Tecnomecanica(
                    vehiculoId = vehiculoId, fechaExpedicion = fechaExp,
                    fechaVencimiento = fechaExp.plusYears(1), valor = valorNum, lugar = lugar, fotoPath = foto
                )
            )
            alCerrar()
        },
        alCancelar = alCerrar
    ) {
        CampoFecha("Fecha de expedición", fechaExp) { fechaExp = it }
        Text("Vence: ${fechaExp.plusYears(1)}", style = MaterialTheme.typography.bodySmall)
        CampoTexto("Valor", valor, { valor = it }, numerico = true)
        CampoTexto("Lugar", lugar) { lugar = it }
        CampoFoto(foto) { foto = it }
    }
}

@Composable
private fun DialogoSoat(viewModel: BitacoraViewModel, vehiculoId: Long, alCerrar: () -> Unit) {
    var fechaExp by remember { mutableStateOf(LocalDate.now()) }
    var valor by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf<String?>(null) }

    DialogoFormulario(
        titulo = "Nuevo SOAT",
        alConfirmar = {
            val valorNum = valor.toDoubleOrNull() ?: return@DialogoFormulario
            if (lugar.isBlank()) return@DialogoFormulario
            viewModel.agregarSoat(
                Soat(
                    vehiculoId = vehiculoId, fechaExpedicion = fechaExp,
                    fechaVencimiento = fechaExp.plusYears(1), valor = valorNum, lugar = lugar, fotoPath = foto
                )
            )
            alCerrar()
        },
        alCancelar = alCerrar
    ) {
        CampoFecha("Fecha de expedición", fechaExp) { fechaExp = it }
        Text("Vence: ${fechaExp.plusYears(1)}", style = MaterialTheme.typography.bodySmall)
        CampoTexto("Valor", valor, { valor = it }, numerico = true)
        CampoTexto("Lugar", lugar) { lugar = it }
        CampoFoto(foto) { foto = it }
    }
}
