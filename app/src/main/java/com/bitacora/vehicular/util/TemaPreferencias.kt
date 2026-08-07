package com.bitacora.vehicular.util

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Guarda el color de fondo elegido por el usuario en SharedPreferences y lo
 * expone como estado observable de Compose para que el tema se actualice al instante.
 */
object TemaPreferencias {
    private const val PREFS = "tema_prefs"
    private const val KEY_COLOR = "color_fondo_argb"

    var colorFondo by mutableStateOf<Color?>(null)
        private set

    fun cargar(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val valor = prefs.getInt(KEY_COLOR, 0)
        colorFondo = if (valor != 0) Color(valor) else null
    }

    fun establecer(context: Context, color: Color?) {
        colorFondo = color
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (color == null) {
            prefs.edit().remove(KEY_COLOR).apply()
        } else {
            prefs.edit().putInt(KEY_COLOR, color.toArgb()).apply()
        }
    }
}
