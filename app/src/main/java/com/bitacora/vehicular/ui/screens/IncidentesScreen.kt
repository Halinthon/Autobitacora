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
import com.bitacora.vehicular.data.Incidente
import com.bitacora.vehicular.data.TipoIncidente
import com.bitacora.vehicular.ui.components.*
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import java.time.LocalDate

private fun nombreTipoIncidente(t: TipoIncidente) = when (t) {
    TipoIncidente.PINCHAZO -> "Pinchazo"
    TipoIncidente.MECANICA -> "Mecánica"
    TipoIncidente.ELECTRICO -> "Eléctrico"
    TipoIncidente.GOLPE -> "Golpe"
}

@Composable
fun IncidentesScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    val lista by viewModel.incidentes.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Incidente?>(null) }

    if (vehiculo == null) { SinVehiculoAviso(); return }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editando = null; mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar") }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            items(lista, key = { it.id }) { i ->
                TarjetaRegistro(
                    titulo = "${i.fecha} · ${nombreTipoIncidente(i.tipo)}",
                    subtitulo = i.lugar,
                    fotoPath = i.fotoPath,
                    alEditar = { editando = i; mostrarDialogo = true },
                    alEliminar = { viewModel.eliminarIncidente(i) }
                )
            }
        }
    }

    if (mostrarDialogo) {
        var fecha by remember { mutableStateOf(editando?.fecha ?: LocalDate.now()) }
        var tipo by remember { mutableStateOf(editando?.tipo ?: TipoIncidente.MECANICA) }
        var lugar by remember { mutableStateOf(editando?.lugar ?: "") }
        var foto by remember { mutableStateOf(editando?.fotoPath) }

        DialogoFormulario(
            titulo = if (editando == null) "Nuevo incidente" else "Editar incidente",
            alConfirmar = {
                if (lugar.isBlank()) return@DialogoFormulario
                if (editando == null) {
                    viewModel.agregarIncidente(Incidente(vehiculoId = vehiculo!!.id, fecha = fecha, tipo = tipo, lugar = lugar, fotoPath = foto))
                } else {
                    viewModel.editarIncidente(editando!!.copy(fecha = fecha, tipo = tipo, lugar = lugar, fotoPath = foto))
                }
                mostrarDialogo = false
            },
            alCancelar = { mostrarDialogo = false }
        ) {
            CampoFecha("Fecha", fecha) { fecha = it }
            CampoSeleccion("Tipo de incidente", TipoIncidente.entries, tipo, ::nombreTipoIncidente) { tipo = it }
            CampoTexto("Lugar", lugar) { lugar = it }
            CampoFoto(foto) { foto = it }
        }
    }
}
