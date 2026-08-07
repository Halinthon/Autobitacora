package com.bitacora.vehicular.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

/**
 * Copia una foto seleccionada desde la galería (Uri temporal) hacia el
 * almacenamiento interno de la app, para que quede disponible aunque el
 * usuario borre la foto original de su galería.
 */
object FotoUtil {

    fun copiarAAlmacenamientoInterno(context: Context, uri: Uri): String? {
        return try {
            val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
            val destino = File(carpeta, "${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                destino.outputStream().use { output -> input.copyTo(output) }
            }
            destino.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun eliminar(path: String?) {
        if (path.isNullOrBlank()) return
        runCatching { File(path).delete() }
    }
}
