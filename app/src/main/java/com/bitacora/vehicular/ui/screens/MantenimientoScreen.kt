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
import com.bitacora.vehicular.data.CambioAceite
import com.bitacora.vehicular.data.CompraAutoparte
import com.bitacora.vehicular.data.Reparacion
import com.bitacora.vehicular.ui.components.*
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import java.time.LocalDate

private const val KM_INTERVALO_ACEITE = 5000

@Composable
fun MantenimientoScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    var tab by remember { mutableStateOf(0) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    if (vehiculo == null) {
        SinVehiculoAviso()
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar") }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Aceite") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Reparación") })
                Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Autopartes") })
            }
            when (tab) {
                0 -> ListaCambiosAceite(viewModel)
                1 -> ListaReparaciones(viewModel)
                2 -> ListaAutopartes(viewModel)
            }
        }
    }

    if (mostrarDialogo) {
        when (tab) {
            0 -> DialogoCambioAceite(viewModel, vehiculo!!.id) { mostrarDialogo = false }
            1 -> DialogoReparacion(viewModel, vehiculo!!.id) { mostrarDialogo = false }
            2 -> DialogoAutoparte(viewModel, vehiculo!!.id) { mostrarDialogo = false }
        }
    }
}

@Composable
private fun ListaCambiosAceite(viewModel: BitacoraViewModel) {
    val lista by viewModel.cambiosAceite.collectAsState()
    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(lista, key = { it.id }) { c ->
            TarjetaRegistro(
                titulo = "${c.fecha} · ${c.kilometraje} km",
                subtitulo = "Próximo: ${c.proximoCambioKm} km · ${c.lugar}",
                detalle = "$${"%,.0f".format(c.costo)}",
                fotoPath = c.fotoPath,
                alEliminar = { viewModel.eliminarCambioAceite(c) }
            )
        }
    }
}

@Composable
private fun ListaReparaciones(viewModel: BitacoraViewModel) {
    val lista by viewModel.reparaciones.collectAsState()
    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(lista, key = { it.id }) { r ->
            TarjetaRegistro(
                titulo = "${r.fecha} · ${r.lugar}",
                subtitulo = r.resumen,
                detalle = "$${"%,.0f".format(r.costo)}",
                fotoPath = r.fotoPath,
                alEliminar = { viewModel.eliminarReparacion(r) }
            )
        }
    }
}

@Composable
private fun ListaAutopartes(viewModel: BitacoraViewModel) {
    val lista by viewModel.compraAutopartes.collectAsState()
    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(lista, key = { it.id }) { c ->
            TarjetaRegistro(
                titulo = "${c.fecha} · ${c.nombreParte}",
                subtitulo = c.lugar,
                detalle = "$${"%,.0f".format(c.costo)}",
                fotoPath = c.fotoPath,
                alEliminar = { viewModel.eliminarCompraAutoparte(c) }
            )
        }
    }
}

@Composable
private fun DialogoCambioAceite(viewModel: BitacoraViewModel, vehiculoId: Long, alCerrar: () -> Unit) {
    var fecha by remember { mutableStateOf(LocalDate.now()) }
    var km by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf<String?>(null) }

    DialogoFormulario(
        titulo = "Nuevo cambio de aceite",
        alConfirmar = {
            val kmValor = km.toIntOrNull() ?: return@DialogoFormulario
            val costoValor = costo.toDoubleOrNull() ?: return@DialogoFormulario
            if (lugar.isBlank()) return@DialogoFormulario
            viewModel.agregarCambioAceite(
                CambioAceite(
                    vehiculoId = vehiculoId, fecha = fecha, kilometraje = kmValor,
                    proximoCambioKm = kmValor + KM_INTERVALO_ACEITE, costo = costoValor, lugar = lugar, fotoPath = foto
                )
            )
            alCerrar()
        },
        alCancelar = alCerrar
    ) {
        CampoFecha("Fecha", fecha) { fecha = it }
        CampoTexto("Kilometraje", km, { km = it.filter { c -> c.isDigit() } }, numerico = true)
        if (km.toIntOrNull() != null) Text("Próximo cambio sugerido: ${km.toInt() + KM_INTERVALO_ACEITE} km", style = MaterialTheme.typography.bodySmall)
        CampoTexto("Costo", costo, { costo = it }, numerico = true)
        CampoTexto("Lugar", lugar) { lugar = it }
        CampoFoto(foto) { foto = it }
    }
}

@Composable
private fun DialogoReparacion(viewModel: BitacoraViewModel, vehiculoId: Long, alCerrar: () -> Unit) {
    var fecha by remember { mutableStateOf(LocalDate.now()) }
    var resumen by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf<String?>(null) }

    DialogoFormulario(
        titulo = "Nueva reparación",
        alConfirmar = {
            val costoValor = costo.toDoubleOrNull() ?: return@DialogoFormulario
            if (resumen.isBlank() || lugar.isBlank()) return@DialogoFormulario
            viewModel.agregarReparacion(Reparacion(vehiculoId = vehiculoId, fecha = fecha, resumen = resumen, costo = costoValor, lugar = lugar, fotoPath = foto))
            alCerrar()
        },
        alCancelar = alCerrar
    ) {
        CampoFecha("Fecha", fecha) { fecha = it }
        CampoTexto("Resumen de la reparación", resumen) { resumen = it }
        CampoTexto("Costo", costo, { costo = it }, numerico = true)
        CampoTexto("Lugar", lugar) { lugar = it }
        CampoFoto(foto) { foto = it }
    }
}

@Composable
private fun DialogoAutoparte(viewModel: BitacoraViewModel, vehiculoId: Long, alCerrar: () -> Unit) {
    var fecha by remember { mutableStateOf(LocalDate.now()) }
    var nombre by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf<String?>(null) }

    DialogoFormulario(
        titulo = "Nueva compra de autoparte",
        alConfirmar = {
            val costoValor = costo.toDoubleOrNull() ?: return@DialogoFormulario
            if (nombre.isBlank() || lugar.isBlank()) return@DialogoFormulario
            viewModel.agregarCompraAutoparte(CompraAutoparte(vehiculoId = vehiculoId, fecha = fecha, nombreParte = nombre, costo = costoValor, lugar = lugar, fotoPath = foto))
            alCerrar()
        },
        alCancelar = alCerrar
    ) {
        CampoFecha("Fecha", fecha) { fecha = it }
        CampoTexto("Nombre de la parte", nombre) { nombre = it }
        CampoTexto("Costo", costo, { costo = it }, numerico = true)
        CampoTexto("Lugar", lugar) { lugar = it }
        CampoFoto(foto) { foto = it }
    }
}

@Composable
fun SinVehiculoAviso() {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text("Primero registra o selecciona un vehículo en la sección Vehículo.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
