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
import com.bitacora.vehicular.data.OtroPago
import com.bitacora.vehicular.ui.components.*
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import java.time.LocalDate

@Composable
fun OtrosPagosScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    val lista by viewModel.otrosPagos.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<OtroPago?>(null) }

    if (vehiculo == null) { SinVehiculoAviso(); return }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editando = null; mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar") }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            items(lista, key = { it.id }) { o ->
                TarjetaRegistro(
                    titulo = "${o.fecha} · ${o.descripcion}",
                    subtitulo = "",
                    detalle = "$${"%,.0f".format(o.valor)}",
                    fotoPath = o.fotoPath,
                    alEditar = { editando = o; mostrarDialogo = true },
                    alEliminar = { viewModel.eliminarOtroPago(o) }
                )
            }
        }
    }

    if (mostrarDialogo) {
        var fecha by remember { mutableStateOf(editando?.fecha ?: LocalDate.now()) }
        var descripcion by remember { mutableStateOf(editando?.descripcion ?: "") }
        var valor by remember { mutableStateOf(editando?.valor?.toString() ?: "") }
        var foto by remember { mutableStateOf(editando?.fotoPath) }

        DialogoFormulario(
            titulo = if (editando == null) "Nuevo pago" else "Editar pago",
            alConfirmar = {
                val valorNum = valor.toDoubleOrNull() ?: return@DialogoFormulario
                if (descripcion.isBlank()) return@DialogoFormulario
                if (editando == null) {
                    viewModel.agregarOtroPago(OtroPago(vehiculoId = vehiculo!!.id, descripcion = descripcion, fecha = fecha, valor = valorNum, fotoPath = foto))
                } else {
                    viewModel.editarOtroPago(editando!!.copy(descripcion = descripcion, fecha = fecha, valor = valorNum, fotoPath = foto))
                }
                mostrarDialogo = false
            },
            alCancelar = { mostrarDialogo = false }
        ) {
            CampoTexto("Descripción", descripcion) { descripcion = it }
            CampoFecha("Fecha", fecha) { fecha = it }
            CampoTexto("Valor", valor, numerico = true) { valor = it }
            CampoFoto(foto) { foto = it }
        }
    }
}
