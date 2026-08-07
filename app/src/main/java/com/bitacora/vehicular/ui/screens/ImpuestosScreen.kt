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
import com.bitacora.vehicular.data.Impuesto
import com.bitacora.vehicular.data.MedioPago
import com.bitacora.vehicular.ui.components.*
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import java.time.LocalDate

private fun nombreMedioPago(m: MedioPago) = if (m == MedioPago.DIGITAL) "Digital" else "Presencial"

@Composable
fun ImpuestosScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    val lista by viewModel.impuestos.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Impuesto?>(null) }

    if (vehiculo == null) { SinVehiculoAviso(); return }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editando = null; mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar") }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            items(lista, key = { it.id }) { i ->
                TarjetaRegistro(
                    titulo = "${i.fechaPago} · Recibo ${i.nroRecibo}",
                    subtitulo = nombreMedioPago(i.medioPago),
                    detalle = "$${"%,.0f".format(i.valor)}",
                    fotoPath = i.fotoPath,
                    alEditar = { editando = i; mostrarDialogo = true },
                    alEliminar = { viewModel.eliminarImpuesto(i) }
                )
            }
        }
    }

    if (mostrarDialogo) {
        var fecha by remember { mutableStateOf(editando?.fechaPago ?: LocalDate.now()) }
        var valor by remember { mutableStateOf(editando?.valor?.toString() ?: "") }
        var recibo by remember { mutableStateOf(editando?.nroRecibo ?: "") }
        var medio by remember { mutableStateOf(editando?.medioPago ?: MedioPago.DIGITAL) }
        var foto by remember { mutableStateOf(editando?.fotoPath) }

        DialogoFormulario(
            titulo = if (editando == null) "Nuevo pago de impuesto" else "Editar pago de impuesto",
            alConfirmar = {
                val valorNum = valor.toDoubleOrNull() ?: return@DialogoFormulario
                if (recibo.isBlank()) return@DialogoFormulario
                if (editando == null) {
                    viewModel.agregarImpuesto(Impuesto(vehiculoId = vehiculo!!.id, fechaPago = fecha, valor = valorNum, nroRecibo = recibo, medioPago = medio, fotoPath = foto))
                } else {
                    viewModel.editarImpuesto(editando!!.copy(fechaPago = fecha, valor = valorNum, nroRecibo = recibo, medioPago = medio, fotoPath = foto))
                }
                mostrarDialogo = false
            },
            alCancelar = { mostrarDialogo = false }
        ) {
            CampoFecha("Fecha de pago", fecha) { fecha = it }
            CampoTexto("Valor", valor, numerico = true) { valor = it }
            CampoTexto("Nro. de recibo", recibo) { recibo = it }
            CampoSeleccion("Medio de pago", MedioPago.entries, medio, ::nombreMedioPago) { medio = it }
            CampoFoto(foto) { foto = it }
        }
    }
}
