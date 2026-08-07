@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bitacora.vehicular.ui.components

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.bitacora.vehicular.util.FotoUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/**
 * Campo de fecha. Se implementa con un TextField deshabilitado (no readOnly) y una
 * capa "clickable" encima, porque un OutlinedTextField de solo lectura intercepta
 * los toques para su propio manejo de cursor/foco y puede impedir que el clic
 * externo abra el DatePickerDialog de forma confiable.
 */
@Composable
fun CampoFecha(etiqueta: String, fecha: LocalDate, alCambiar: (LocalDate) -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                DatePickerDialog(
                    context,
                    { _, y, m, d -> alCambiar(LocalDate.of(y, m + 1, d)) },
                    fecha.year, fecha.monthValue - 1, fecha.dayOfMonth
                ).show()
            }
    ) {
        OutlinedTextField(
            value = fecha.format(formatoFecha),
            onValueChange = {},
            enabled = false,
            label = { Text(etiqueta) },
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CampoTexto(etiqueta: String, valor: String, numerico: Boolean = false, alCambiar: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        singleLine = true,
        keyboardOptions = if (numerico) androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number) else androidx.compose.foundation.text.KeyboardOptions.Default,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun <T> CampoSeleccion(etiqueta: String, opciones: List<T>, seleccion: T, textoOpcion: (T) -> String, alSeleccionar: (T) -> Unit) {
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = textoOpcion(seleccion),
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(text = { Text(textoOpcion(opcion)) }, onClick = {
                    alSeleccionar(opcion)
                    expandido = false
                })
            }
        }
    }
}

/** Selector de foto opcional para fichas (mantenimiento, documentos, etc.). Permite ver, cambiar o quitar. */
@Composable
fun CampoFoto(fotoPath: String?, alSeleccionar: (String?) -> Unit) {
    val context = LocalContext.current
    var mostrarVisor by remember { mutableStateOf(false) }
    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) alSeleccionar(FotoUtil.copiarAAlmacenamientoInterno(context, uri))
    }
    Column {
        Text("Foto / comprobante (opcional)", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        if (fotoPath != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = java.io.File(fotoPath), contentDescription = "Ver foto",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { mostrarVisor = true }
                )
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { lanzador.launch("image/*") }) { Text("Cambiar") }
                TextButton(onClick = { alSeleccionar(null) }) { Text("Quitar") }
            }
        } else {
            OutlinedButton(onClick = { lanzador.launch("image/*") }) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Adjuntar foto")
            }
        }
    }
    if (mostrarVisor && fotoPath != null) {
        VisorFoto(fotoPath) { mostrarVisor = false }
    }
}

/** Selector circular de foto de perfil del vehículo. */
@Composable
fun CampoFotoVehiculo(fotoPath: String?, alSeleccionar: (String?) -> Unit) {
    val context = LocalContext.current
    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) alSeleccionar(FotoUtil.copiarAAlmacenamientoInterno(context, uri))
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { lanzador.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (fotoPath != null) {
                AsyncImage(
                    model = java.io.File(fotoPath), contentDescription = "Foto del vehículo",
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Agregar foto", modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(4.dp))
        Row {
            TextButton(onClick = { lanzador.launch("image/*") }) { Text(if (fotoPath == null) "Agregar foto" else "Cambiar foto") }
            if (fotoPath != null) TextButton(onClick = { alSeleccionar(null) }) { Text("Quitar") }
        }
    }
}

/** Visor de foto a pantalla completa (se cierra tocando fuera o el botón X). */
@Composable
fun VisorFoto(fotoPath: String, alCerrar: () -> Unit) {
    Dialog(onDismissRequest = alCerrar) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            AsyncImage(
                model = java.io.File(fotoPath), contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
            IconButton(onClick = alCerrar, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }
    }
}

@Composable
fun TarjetaRegistro(
    titulo: String,
    subtitulo: String,
    detalle: String? = null,
    fotoPath: String? = null,
    alEditar: (() -> Unit)? = null,
    alEliminar: () -> Unit
) {
    var mostrarVisor by remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .let { if (alEditar != null) it.clickable { alEditar() } else it }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (fotoPath != null) {
                AsyncImage(
                    model = java.io.File(fotoPath), contentDescription = "Ver foto",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { mostrarVisor = true }
                )
                Spacer(Modifier.width(10.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleSmall)
                Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (detalle != null) Text(detalle, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = alEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
    if (mostrarVisor && fotoPath != null) {
        VisorFoto(fotoPath) { mostrarVisor = false }
    }
}

@Composable
fun TarjetaAlerta(titulo: String, detalle: String, esUrgente: Boolean) {
    val color = if (esUrgente) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(color, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Warning, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Column {
            Text(titulo, style = MaterialTheme.typography.bodyMedium)
            Text(detalle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun DialogoFormulario(
    titulo: String,
    alConfirmar: () -> Unit,
    alCancelar: () -> Unit,
    contenido: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text(titulo) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(max = 460.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = contenido
            )
        },
        confirmButton = { TextButton(onClick = alConfirmar) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = alCancelar) { Text("Cancelar") } }
    )
}
