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
import com.bitacora.vehicular.data.TarjetaPropiedad
import com.bitacora.vehicular.ui.components.CampoFecha
import com.bitacora.vehicular.ui.components.CampoFoto
import com.bitacora.vehicular.ui.components.CampoTexto
import com.bitacora.vehicular.ui.components.DialogoFormulario
import com.bitacora.vehicular.ui.components.TarjetaRegistro
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import java.time.LocalDate

@Composable
fun TarjetaPropiedadScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    val lista by viewModel.tarjetasPropiedad.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<TarjetaPropiedad?>(null) }

    if (vehiculo == null) { SinVehiculoAviso(); return }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editando = null; mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar") }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            items(lista, key = { it.id }) { t ->
                TarjetaRegistro(
                    titulo = "Propietario: ${t.propietario}",
                    subtitulo = "C.C. ${t.cedula} · ${t.servicio}",
                    detalle = "Matrícula: ${t.fechaMatricula} · ${t.organismoTransito}",
                    fotoPath = t.fotoPath,
                    alEditar = { editando = t; mostrarDialogo = true },
                    alEliminar = { viewModel.eliminarTarjetaPropiedad(t) }
                )
            }
        }
    }

    if (mostrarDialogo) {
        var numeroMotor by remember { mutableStateOf(editando?.numeroMotor ?: "") }
        var vin by remember { mutableStateOf(editando?.vin ?: "") }
        var chasis by remember { mutableStateOf(editando?.chasis ?: "") }
        var propietario by remember { mutableStateOf(editando?.propietario ?: "") }
        var cedula by remember { mutableStateOf(editando?.cedula ?: "") }
        var servicio by remember { mutableStateOf(editando?.servicio ?: "") }
        var fechaMatricula by remember { mutableStateOf(editando?.fechaMatricula ?: LocalDate.now()) }
        var fechaExpedicion by remember { mutableStateOf(editando?.fechaExpedicion ?: LocalDate.now()) }
        var organismo by remember { mutableStateOf(editando?.organismoTransito ?: "") }
        var foto by remember { mutableStateOf(editando?.fotoPath) }

        DialogoFormulario(
            titulo = if (editando == null) "Nueva tarjeta de propiedad" else "Editar tarjeta de propiedad",
            alConfirmar = {
                if (numeroMotor.isBlank() || vin.isBlank() || chasis.isBlank() || propietario.isBlank() || cedula.isBlank() || organismo.isBlank()) return@DialogoFormulario
                if (editando == null) {
                    viewModel.agregarTarjetaPropiedad(
                        TarjetaPropiedad(
                            vehiculoId = vehiculo!!.id, numeroMotor = numeroMotor, vin = vin, chasis = chasis,
                            propietario = propietario, cedula = cedula, servicio = servicio,
                            fechaMatricula = fechaMatricula, fechaExpedicion = fechaExpedicion,
                            organismoTransito = organismo, fotoPath = foto
                        )
                    )
                } else {
                    viewModel.editarTarjetaPropiedad(
                        editando!!.copy(
                            numeroMotor = numeroMotor, vin = vin, chasis = chasis, propietario = propietario,
                            cedula = cedula, servicio = servicio, fechaMatricula = fechaMatricula,
                            fechaExpedicion = fechaExpedicion, organismoTransito = organismo, fotoPath = foto
                        )
                    )
                }
                mostrarDialogo = false
            },
            alCancelar = { mostrarDialogo = false }
        ) {
            CampoTexto("Número de motor", numeroMotor) { numeroMotor = it }
            CampoTexto("VIN", vin) { vin = it }
            CampoTexto("Chasis", chasis) { chasis = it }
            CampoTexto("Propietario", propietario) { propietario = it }
            CampoTexto("Cédula", cedula) { cedula = it }
            CampoTexto("Servicio (particular, público, etc.)", servicio) { servicio = it }
            CampoFecha("Fecha de matrícula", fechaMatricula) { fechaMatricula = it }
            CampoFecha("Fecha de expedición", fechaExpedicion) { fechaExpedicion = it }
            CampoTexto("Organismo de tránsito", organismo) { organismo = it }
            CampoFoto(foto) { foto = it }
        }
    }
}
