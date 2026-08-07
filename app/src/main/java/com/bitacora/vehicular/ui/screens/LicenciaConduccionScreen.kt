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
import com.bitacora.vehicular.data.LicenciaConduccion
import com.bitacora.vehicular.ui.components.CampoFecha
import com.bitacora.vehicular.ui.components.CampoFoto
import com.bitacora.vehicular.ui.components.CampoTexto
import com.bitacora.vehicular.ui.components.DialogoFormulario
import com.bitacora.vehicular.ui.components.TarjetaRegistro
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import java.time.LocalDate

@Composable
fun LicenciaConduccionScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    val lista by viewModel.licenciasConduccion.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<LicenciaConduccion?>(null) }

    if (vehiculo == null) { SinVehiculoAviso(); return }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editando = null; mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar") }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            items(lista, key = { it.id }) { l ->
                TarjetaRegistro(
                    titulo = "${l.nombre} · Cat. ${l.categoria}",
                    subtitulo = "Nro. ${l.numero} · Vence: ${l.fechaVencimiento}",
                    detalle = if (l.restricciones.isNotBlank()) "Restricciones: ${l.restricciones}" else null,
                    fotoPath = l.fotoPath,
                    alEditar = { editando = l; mostrarDialogo = true },
                    alEliminar = { viewModel.eliminarLicenciaConduccion(l) }
                )
            }
        }
    }

    if (mostrarDialogo) {
        var numero by remember { mutableStateOf(editando?.numero ?: "") }
        var nombre by remember { mutableStateOf(editando?.nombre ?: "") }
        var fechaExpedicion by remember { mutableStateOf(editando?.fechaExpedicion ?: LocalDate.now()) }
        var fechaVencimiento by remember { mutableStateOf(editando?.fechaVencimiento ?: LocalDate.now().plusYears(10)) }
        var categoria by remember { mutableStateOf(editando?.categoria ?: "") }
        var restricciones by remember { mutableStateOf(editando?.restricciones ?: "") }
        var organismo by remember { mutableStateOf(editando?.organismoTransito ?: "") }
        var foto by remember { mutableStateOf(editando?.fotoPath) }

        DialogoFormulario(
            titulo = if (editando == null) "Nueva licencia de conducción" else "Editar licencia de conducción",
            alConfirmar = {
                if (numero.isBlank() || nombre.isBlank() || categoria.isBlank() || organismo.isBlank()) return@DialogoFormulario
                if (editando == null) {
                    viewModel.agregarLicenciaConduccion(
                        LicenciaConduccion(
                            vehiculoId = vehiculo!!.id, numero = numero, nombre = nombre,
                            fechaExpedicion = fechaExpedicion, fechaVencimiento = fechaVencimiento,
                            categoria = categoria, restricciones = restricciones,
                            organismoTransito = organismo, fotoPath = foto
                        )
                    )
                } else {
                    viewModel.editarLicenciaConduccion(
                        editando!!.copy(
                            numero = numero, nombre = nombre, fechaExpedicion = fechaExpedicion,
                            fechaVencimiento = fechaVencimiento, categoria = categoria,
                            restricciones = restricciones, organismoTransito = organismo, fotoPath = foto
                        )
                    )
                }
                mostrarDialogo = false
            },
            alCancelar = { mostrarDialogo = false }
        ) {
            CampoTexto("Número", numero) { numero = it }
            CampoTexto("Nombre", nombre) { nombre = it }
            CampoFecha("Fecha de expedición", fechaExpedicion) { fechaExpedicion = it }
            CampoFecha("Fecha de vencimiento", fechaVencimiento) { fechaVencimiento = it }
            CampoTexto("Categoría", categoria) { categoria = it }
            CampoTexto("Restricciones", restricciones) { restricciones = it }
            CampoTexto("Organismo de tránsito", organismo) { organismo = it }
            CampoFoto(foto) { foto = it }
        }
    }
}
