package com.bitacora.vehicular.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.bitacora.vehicular.viewmodel.BitacoraViewModel
import kotlinx.coroutines.launch

@Composable
fun BackupScreen(viewModel: BitacoraViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mensaje by remember { mutableStateOf<String?>(null) }

    val lanzadorImportar = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            viewModel.importarBackup(uri)
            mensaje = "Importación en proceso. Revisa tus vehículos en unos segundos."
        }
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Respaldo de datos", style = MaterialTheme.typography.titleLarge)
        Text(
            "Toda tu información se guarda solo en este celular. Exporta un respaldo periódicamente para no perder tu historial si cambias de equipo.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = {
                scope.launch {
                    val archivo = viewModel.exportarBackup()
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Compartir respaldo"))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Exportar respaldo (JSON)")
        }

        OutlinedButton(
            onClick = { lanzadorImportar.launch("application/json") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CloudDownload, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Importar respaldo")
        }

        mensaje?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
    }
}
