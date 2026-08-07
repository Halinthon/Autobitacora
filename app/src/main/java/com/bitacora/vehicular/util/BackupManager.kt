package com.bitacora.vehicular.util

import android.content.Context
import android.net.Uri
import com.bitacora.vehicular.data.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.first
import java.io.File
import java.time.LocalDate

/** Estructura completa que se exporta/importa como respaldo (backup) de la app. */
data class BackupCompleto(
    val vehiculos: List<Vehiculo>,
    val cambiosAceite: List<CambioAceite>,
    val reparaciones: List<Reparacion>,
    val comprasAutopartes: List<CompraAutoparte>,
    val incidentes: List<Incidente>,
    val tecnomecanicas: List<Tecnomecanica>,
    val soats: List<Soat>,
    val impuestos: List<Impuesto>,
    val otrosPagos: List<OtroPago>,
    val enlaces: List<Enlace>,
    val registrosOdometro: List<RegistroOdometro>,
    val abastecimientos: List<Abastecimiento> = emptyList(),
    val tarjetasPropiedad: List<TarjetaPropiedad> = emptyList(),
    val licenciasConduccion: List<LicenciaConduccion> = emptyList()
)

class BackupManager(private val context: Context, private val repo: Repository) {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .setPrettyPrinting()
        .create()

    /** Genera el JSON completo con todo el historial de todos los vehículos. */
    suspend fun exportarAArchivo(): File {
        val backup = BackupCompleto(
            vehiculos = repo.vehiculoDao.obtenerTodos().first(),
            cambiosAceite = repo.cambioAceiteDao.obtenerTodos(),
            reparaciones = repo.reparacionDao.obtenerTodos(),
            comprasAutopartes = repo.compraAutoparteDao.obtenerTodos(),
            incidentes = repo.incidenteDao.obtenerTodos(),
            tecnomecanicas = repo.tecnomecanicaDao.obtenerTodas(),
            soats = repo.soatDao.obtenerTodos(),
            impuestos = repo.impuestoDao.obtenerTodos(),
            otrosPagos = repo.otroPagoDao.obtenerTodos(),
            enlaces = repo.enlaceDao.obtenerTodos(),
            registrosOdometro = repo.registroOdometroDao.obtenerTodos(),
            abastecimientos = repo.abastecimientoDao.obtenerTodos(),
            tarjetasPropiedad = repo.tarjetaPropiedadDao.obtenerTodas(),
            licenciasConduccion = repo.licenciaConduccionDao.obtenerTodas()
        )
        val json = gson.toJson(backup)
        val carpeta = File(context.cacheDir, "exportaciones").apply { mkdirs() }
        val archivo = File(carpeta, "bitacora_vehicular_backup.json")
        archivo.writeText(json)
        return archivo
    }

    /**
     * Importa un backup completo. Los vehículos se re-crean con nuevos IDs y todo
     * su historial relacionado se re-vincula a esos nuevos IDs, para evitar
     * choques con datos que ya existan en el dispositivo.
     */
    suspend fun importarDesdeArchivo(uri: Uri) {
        val texto = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            ?: return
        val backup = gson.fromJson(texto, BackupCompleto::class.java)

        val mapaIds = HashMap<Long, Long>() // id antiguo -> id nuevo
        backup.vehiculos.forEach { v ->
            val nuevoId = repo.vehiculoDao.insertar(v.copy(id = 0))
            mapaIds[v.id] = nuevoId
        }

        backup.cambiosAceite.forEach { c -> mapaIds[c.vehiculoId]?.let { repo.cambioAceiteDao.insertar(c.copy(id = 0, vehiculoId = it)) } }
        backup.reparaciones.forEach { r -> mapaIds[r.vehiculoId]?.let { repo.reparacionDao.insertar(r.copy(id = 0, vehiculoId = it)) } }
        backup.comprasAutopartes.forEach { c -> mapaIds[c.vehiculoId]?.let { repo.compraAutoparteDao.insertar(c.copy(id = 0, vehiculoId = it)) } }
        backup.incidentes.forEach { i -> mapaIds[i.vehiculoId]?.let { repo.incidenteDao.insertar(i.copy(id = 0, vehiculoId = it)) } }
        backup.tecnomecanicas.forEach { t -> mapaIds[t.vehiculoId]?.let { repo.tecnomecanicaDao.insertar(t.copy(id = 0, vehiculoId = it)) } }
        backup.soats.forEach { s -> mapaIds[s.vehiculoId]?.let { repo.soatDao.insertar(s.copy(id = 0, vehiculoId = it)) } }
        backup.impuestos.forEach { i -> mapaIds[i.vehiculoId]?.let { repo.impuestoDao.insertar(i.copy(id = 0, vehiculoId = it)) } }
        backup.otrosPagos.forEach { o -> mapaIds[o.vehiculoId]?.let { repo.otroPagoDao.insertar(o.copy(id = 0, vehiculoId = it)) } }
        backup.enlaces.forEach { e -> mapaIds[e.vehiculoId]?.let { repo.enlaceDao.insertar(e.copy(id = 0, vehiculoId = it)) } }
        backup.registrosOdometro.forEach { r -> mapaIds[r.vehiculoId]?.let { repo.registroOdometroDao.insertar(r.copy(id = 0, vehiculoId = it)) } }
        backup.abastecimientos.forEach { a -> mapaIds[a.vehiculoId]?.let { repo.abastecimientoDao.insertar(a.copy(id = 0, vehiculoId = it)) } }
        backup.tarjetasPropiedad.forEach { t -> mapaIds[t.vehiculoId]?.let { repo.tarjetaPropiedadDao.insertar(t.copy(id = 0, vehiculoId = it)) } }
        backup.licenciasConduccion.forEach { l -> mapaIds[l.vehiculoId]?.let { repo.licenciaConduccionDao.insertar(l.copy(id = 0, vehiculoId = it)) } }
    }
}

private class LocalDateAdapter : com.google.gson.JsonSerializer<LocalDate>, com.google.gson.JsonDeserializer<LocalDate> {
    override fun serialize(src: LocalDate?, typeOfSrc: java.lang.reflect.Type?, context: com.google.gson.JsonSerializationContext?) =
        com.google.gson.JsonPrimitive(src.toString())

    override fun deserialize(json: com.google.gson.JsonElement?, typeOfT: java.lang.reflect.Type?, context: com.google.gson.JsonDeserializationContext?): LocalDate =
        LocalDate.parse(json?.asString)
}
