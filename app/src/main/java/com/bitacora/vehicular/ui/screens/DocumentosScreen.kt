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
    var tecnoEditando by remember { mutableStateOf<Tecnomecanica?>(null) }
    var soatEditando by remember { mutableStateOf<Soat?>(null) }

    if (vehiculo == null) { SinVehiculoAviso(); return }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { tecnoEditando = null; soatEditando = null; mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar") }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Tecnomecánica") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("SOAT") })
            }
            when (tab) {
                0 -> ListaTecnomecanica(viewModel) { tecnoEditando = it; mostrarDialogo = true }
                1 -> ListaSoat(viewModel) { soatEditando = it; mostrarDialogo = true }
            }
        }
    }

    if (mostrarDialogo) {
        when (tab) {
            0 -> DialogoTecnomecanica(viewModel, vehiculo!!.id, tecnoEditando) { mostrarDialogo = false }
            1 -> DialogoSoat(viewModel, vehiculo!!.id, soatEditando) { mostrarDialogo = false }
        }
    }
}

@Composable
private fun ListaTecnomecanica(viewModel: BitacoraViewModel, alEditar: (Tecnomecanica) -> Unit) {
    val lista by viewModel.tecnomecanicas.collectAsState()
    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(lista, key = { it.id }) { t ->
            TarjetaRegistro(
                titulo = "Vence: ${t.fechaVencimiento}",
                subtitulo = "Expedida: ${t.fechaExpedicion} · ${t.lugar}",
                detalle = "$${"%,.0f".format(t.valor)}",
                fotoPath = t.fotoPath,
                alEditar = { alEditar(t) },
                alEliminar = { viewModel.eliminarTecnomecanica(t) }
            )
        }
    }
}

@Composable
private fun ListaSoat(viewModel: BitacoraViewModel, alEditar: (Soat) -> Unit) {
    val lista by viewModel.soats.collectAsState()
    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(lista, key = { it.id }) { s ->
            TarjetaRegistro(
                titulo = "Vence: ${s.fechaVencimiento}",
                subtitulo = "Expedido: ${s.fechaExpedicion} · ${s.lugar}",
                detalle = "$${"%,.0f".format(s.valor)}",
                fotoPath = s.fotoPath,
                alEditar = { alEditar(s) },
                alEliminar = { viewModel.eliminarSoat(s) }
            )
        }
    }
}

@Composable
private fun DialogoTecnomecanica(viewModel: BitacoraViewModel, vehiculoId: Long, editando: Tecnomecanica?, alCerrar: () -> Unit) {
    var fechaExp by remember { mutableStateOf(editando?.fechaExpedicion ?: LocalDate.now()) }
    var valor by remember { mutableStateOf(editando?.valor?.toString() ?: "") }
    var lugar by remember { mutableStateOf(editando?.lugar ?: "") }
    var foto by remember { mutableStateOf(editando?.fotoPath) }

    DialogoFormulario(
        titulo = if (editando == null) "Nueva tecnomecánica" else "Editar tecnomecánica",
        alConfirmar = {
            val valorNum = valor.toDoubleOrNull() ?: return@DialogoFormulario
            if (lugar.isBlank()) return@DialogoFormulario
            if (editando == null) {
                viewModel.agregarTecnomecanica(
                    Tecnomecanica(vehiculoId = vehiculoId, fechaExpedicion = fechaExp, fechaVencimiento = fechaExp.plusYears(1), valor = valorNum, lugar = lugar, fotoPath = foto)
                )
            } else {
                viewModel.editarTecnomecanica(editando.copy(fechaExpedicion = fechaExp, fechaVencimiento = fechaExp.plusYears(1), valor = valorNum, lugar = lugar, fotoPath = foto))
            }
            alCerrar()
        },
        alCancelar = alCerrar
    ) {
        CampoFecha("Fecha de expedición", fechaExp) { fechaExp = it }
        Text("Vence: ${fechaExp.plusYears(1)}", style = MaterialTheme.typography.bodySmall)
        CampoTexto("Valor", valor, numerico = true) { valor = it }
        CampoTexto("Lugar", lugar) { lugar = it }
        CampoFoto(foto) { foto = it }
    }
}

@Composable
private fun DialogoSoat(viewModel: BitacoraViewModel, vehiculoId: Long, editando: Soat?, alCerrar: () -> Unit) {
    var fechaExp by remember { mutableStateOf(editando?.fechaExpedicion ?: LocalDate.now()) }
    var valor by remember { mutableStateOf(editando?.valor?.toString() ?: "") }
    var lugar by remember { mutableStateOf(editando?.lugar ?: "") }
    var foto by remember { mutableStateOf(editando?.fotoPath) }

    DialogoFormulario(
        titulo = if (editando == null) "Nuevo SOAT" else "Editar SOAT",
        alConfirmar = {
            val valorNum = valor.toDoubleOrNull() ?: return@DialogoFormulario
            if (lugar.isBlank()) return@DialogoFormulario
            if (editando == null) {
                viewModel.agregarSoat(
                    Soat(vehiculoId = vehiculoId, fechaExpedicion = fechaExp, fechaVencimiento = fechaExp.plusYears(1), valor = valorNum, lugar = lugar, fotoPath = foto)
                )
            } else {
                viewModel.editarSoat(editando.copy(fechaExpedicion = fechaExp, fechaVencimiento = fechaExp.plusYears(1), valor = valorNum, lugar = lugar, fotoPath = foto))
            }
            alCerrar()
        },
        alCancelar = alCerrar
    ) {
        CampoFecha("Fecha de expedición", fechaExp) { fechaExp = it }
        Text("Vence: ${fechaExp.plusYears(1)}", style = MaterialTheme.typography.bodySmall)
        CampoTexto("Valor", valor, numerico = true) { valor = it }
        CampoTexto("Lugar", lugar) { lugar = it }
        CampoFoto(foto) { foto = it }
    }
}
