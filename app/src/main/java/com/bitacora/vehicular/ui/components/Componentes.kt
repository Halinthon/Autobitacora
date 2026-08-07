package com.bitacora.vehicular.ui.components

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bitacora.vehicular.util.FotoUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun CampoFecha(etiqueta: String, fecha: LocalDate, alCambiar: (LocalDate) -> Unit) {
    val context = LocalContext.current
    OutlinedTextField(
        value = fecha.format(formatoFecha),
        onValueChange = {},
        readOnly = true,
        label = { Text(etiqueta) },
        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                DatePickerDialog(
                    context,
                    { _, y, m, d -> alCambiar(LocalDate.of(y, m + 1, d)) },
                    fecha.year, fecha.monthValue - 1, fecha.dayOfMonth
                ).show()
            }
    )
}

@Composable
fun CampoTexto(etiqueta: String, valor: String, alCambiar: (String) -> Unit, numerico: Boolean = false) {
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

/** Selector de foto opcional. Muestra la miniatura si ya hay una, o un botón para adjuntar. */
@Composable
fun CampoFoto(fotoPath: String?, alSeleccionar: (String?) -> Unit) {
    val context = LocalContext.current
    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) alSeleccionar(FotoUtil.copiarAAlmacenamientoInterno(context, uri))
    }
    Column {
        Text("Foto / comprobante (opcional)", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        if (fotoPath != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = java.io.File(fotoPath), contentDescription = null,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(8.dp))
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
}

@Composable
fun TarjetaRegistro(
    titulo: String,
    subtitulo: String,
    detalle: String? = null,
    fotoPath: String? = null,
    alEliminar: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (fotoPath != null) {
                AsyncImage(
                    model = java.io.File(fotoPath), contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
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
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp), content = contenido) },
        confirmButton = { TextButton(onClick = alConfirmar) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = alCancelar) { Text("Cancelar") } }
    )
}
