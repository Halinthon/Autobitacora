package com.bitacora.vehicular.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.bitacora.vehicular.data.Enlace
import com.bitacora.vehicular.ui.components.CampoTexto
import com.bitacora.vehicular.ui.components.DialogoFormulario
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import java.time.LocalDate

@Composable
fun EnlacesScreen(viewModel: BitacoraViewModel) {
    val vehiculo by viewModel.vehiculoSeleccionado.collectAsState()
    val lista by viewModel.enlaces.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Enlace?>(null) }
    val uriHandler = LocalUriHandler.current

    if (vehiculo == null) { SinVehiculoAviso(); return }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editando = null; mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar enlace") }
        }
    ) { padding ->
        if (lista.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Guarda aquí links de interés: aseguradoras, trámites, talleres, etc.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
                items(lista, key = { it.id }) { e ->
                    ElevatedCard(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { editando = e; mostrarDialogo = true }
                    ) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Link, contentDescription = null)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(e.titulo, style = MaterialTheme.typography.titleSmall)
                                Text(e.categoria, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (e.nota.isNotBlank()) Text(e.nota, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { runCatching { uriHandler.openUri(e.url) } }) {
                                Icon(Icons.Default.OpenInNew, contentDescription = "Abrir enlace")
                            }
                            IconButton(onClick = { viewModel.eliminarEnlace(e) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        var titulo by remember { mutableStateOf(editando?.titulo ?: "") }
        var url by remember { mutableStateOf(editando?.url ?: "") }
        var categoria by remember { mutableStateOf(editando?.categoria ?: "") }
        var nota by remember { mutableStateOf(editando?.nota ?: "") }

        DialogoFormulario(
            titulo = if (editando == null) "Nuevo enlace" else "Editar enlace",
            alConfirmar = {
                if (titulo.isBlank() || url.isBlank()) return@DialogoFormulario
                if (editando == null) {
                    viewModel.agregarEnlace(Enlace(vehiculoId = vehiculo!!.id, titulo = titulo, url = url, nota = nota, fechaGuardado = LocalDate.now(), categoria = categoria.ifBlank { "General" }))
                } else {
                    viewModel.editarEnlace(editando!!.copy(titulo = titulo, url = url, nota = nota, categoria = categoria.ifBlank { "General" }))
                }
                mostrarDialogo = false
            },
            alCancelar = { mostrarDialogo = false }
        ) {
            CampoTexto("Título", titulo) { titulo = it }
            CampoTexto("URL", url) { url = it }
            CampoTexto("Categoría (ej. Seguro, Trámite, Taller)", categoria) { categoria = it }
            CampoTexto("Nota", nota) { nota = it }
        }
    }
}
