package com.bitacora.vehicular.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.bitacora.vehicular.data.Abastecimiento
import com.bitacora.vehicular.ui.components.CampoFecha
import com.bitacora.vehicular.ui.components.CampoTexto
import com.bitacora.vehicular.ui.components.DialogoFormulario
import com.bitacora.vehicular.util.LocationUtil
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

/** true si el texto tiene forma de coordenadas GPS "lat, lon" (las que captura el GPS automáticamente). */
private fun esCoordenadas(texto: String): Boolean =
    Regex("""^-?\d{1,3}\.\d+,\s*-?\d{1,3}\.\d+$""").matches(texto.trim())

@Composable
fun CombustibleScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    val lista by viewModel.abastecimientos.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Abastecimiento?>(null) }
    val uriHandler = LocalUriHandler.current

    if (vehiculo == null) { SinVehiculoAviso(); return }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editando = null; mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar") }
        }
    ) { padding ->
        if (lista.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Aún no has registrado abastecimientos de combustible.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
                items(lista, key = { it.id }) { a ->
                    ElevatedCard(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { editando = a; mostrarDialogo = true }
                    ) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("${a.fecha} · ${a.galones} gal", style = MaterialTheme.typography.titleSmall)
                                Text("${a.kilometraje} km", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (esCoordenadas(a.lugar)) {
                                    Text(
                                        a.lugar,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable {
                                            val partes = a.lugar.split(",").map { it.trim() }
                                            if (partes.size == 2) {
                                                uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=${partes[0]},${partes[1]}")
                                            }
                                        }
                                    )
                                } else {
                                    Text(a.lugar, style = MaterialTheme.typography.bodySmall)
                                }
                                Text("$${"%,.0f".format(a.valor)} ($${"%,.0f".format(a.valorGalon)}/gal)", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { viewModel.eliminarAbastecimiento(a) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoAbastecimiento(viewModel, vehiculo!!.id, editando) { mostrarDialogo = false }
    }
}

@Composable
private fun DialogoAbastecimiento(viewModel: BitacoraViewModel, vehiculoId: Long, editando: Abastecimiento?, alCerrar: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fecha by remember { mutableStateOf(editando?.fecha ?: LocalDate.now()) }
    var valor by remember { mutableStateOf(editando?.valor?.toString() ?: "") }
    var galones by remember { mutableStateOf(editando?.galones?.toString() ?: "") }
    var km by remember { mutableStateOf(editando?.kilometraje?.toString() ?: "") }
    var valorGalon by remember { mutableStateOf(editando?.valorGalon?.toString() ?: "") }
    var lugar by remember { mutableStateOf(editando?.lugar ?: "") }
    var obteniendoUbicacion by remember { mutableStateOf(false) }

    val lanzadorPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            obteniendoUbicacion = true
            scope.launch {
                lugar = LocationUtil.obtenerCoordenadas(context) ?: "No disponible"
                obteniendoUbicacion = false
            }
        } else {
            lugar = "Permiso de ubicación denegado"
        }
    }

    fun capturarUbicacion() {
        lanzadorPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Al abrir el diálogo para un registro nuevo, captura la ubicación automáticamente.
    LaunchedEffect(Unit) {
        if (editando == null) capturarUbicacion()
    }

    DialogoFormulario(
        titulo = if (editando == null) "Nuevo abastecimiento" else "Editar abastecimiento",
        alConfirmar = {
            val valorNum = valor.toDoubleOrNull() ?: return@DialogoFormulario
            val galonesNum = galones.toDoubleOrNull() ?: return@DialogoFormulario
            val kmNum = km.toIntOrNull() ?: return@DialogoFormulario
            val valorGalonNum = valorGalon.toDoubleOrNull() ?: return@DialogoFormulario
            if (editando == null) {
                viewModel.agregarAbastecimiento(
                    Abastecimiento(vehiculoId = vehiculoId, fecha = fecha, valor = valorNum, galones = galonesNum, kilometraje = kmNum, valorGalon = valorGalonNum, lugar = lugar)
                )
            } else {
                viewModel.editarAbastecimiento(editando.copy(fecha = fecha, valor = valorNum, galones = galonesNum, kilometraje = kmNum, valorGalon = valorGalonNum, lugar = lugar))
            }
            alCerrar()
        },
        alCancelar = alCerrar
    ) {
        CampoFecha("Fecha", fecha) { fecha = it }
        CampoTexto("Kilometraje", km, numerico = true) { km = it.filter { c -> c.isDigit() } }
        CampoTexto("Galones", galones, numerico = true) { galones = it }
        CampoTexto("Valor por galón", valorGalon, numerico = true) { valorGalon = it }
        CampoTexto("Valor total", valor, numerico = true) { valor = it }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                if (obteniendoUbicacion) "Obteniendo coordenadas GPS…" else lugar.ifBlank { "Sin ubicación" },
                style = MaterialTheme.typography.bodySmall
            )
        }
        TextButton(onClick = { capturarUbicacion() }) { Text("Actualizar ubicación GPS") }
    }
}
