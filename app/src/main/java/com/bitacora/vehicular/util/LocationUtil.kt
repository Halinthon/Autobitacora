package com.bitacora.vehicular.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

object LocationUtil {

    private fun formatear(loc: Location) = "%.6f, %.6f".format(loc.latitude, loc.longitude)

    /**
     * Devuelve las coordenadas GPS actuales como "lat, lon", o null si no hay
     * permiso, no hay proveedores activos, o no se logra obtener ubicación a tiempo.
     */
    @SuppressLint("MissingPermission")
    suspend fun obtenerCoordenadas(context: Context): String? {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null

        val proveedores = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .filter { runCatching { manager.isProviderEnabled(it) }.getOrDefault(false) }
        if (proveedores.isEmpty()) return null

        // 1) Intentar con la última ubicación conocida (rápido)
        val ultimaConocida = proveedores
            .mapNotNull { runCatching { manager.getLastKnownLocation(it) }.getOrNull() }
            .maxByOrNull { it.time }
        if (ultimaConocida != null) return formatear(ultimaConocida)

        // 2) Si no hay nada en caché, pedir una lectura en vivo con límite de 8 segundos
        return withTimeoutOrNull(8000) {
            suspendCancellableCoroutine<String?> { cont ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        if (cont.isActive) cont.resume(formatear(location))
                        runCatching { manager.removeUpdates(this) }
                    }
                }
                cont.invokeOnCancellation { runCatching { manager.removeUpdates(listener) } }
                runCatching {
                    manager.requestLocationUpdates(proveedores.first(), 0L, 0f, listener, Looper.getMainLooper())
                }.onFailure { if (cont.isActive) cont.resume(null) }
            }
        }
    }
}
