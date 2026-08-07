package com.bitacora.vehicular.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bitacora.vehicular.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Alerta(val titulo: String, val detalle: String, val esUrgente: Boolean)

/** Rango de fechas opcional para filtrar el resumen de gastos del dashboard. */
data class RangoFechas(val desde: LocalDate, val hasta: LocalDate)

class BitacoraViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = Repository(app)
    val backupManager = com.bitacora.vehicular.util.BackupManager(app, repo)

    // ---------- Vehículos ----------
    val vehiculos: StateFlow<List<Vehiculo>> = repo.vehiculoDao.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _vehiculoSeleccionadoId = MutableStateFlow<Long?>(null)
    val vehiculoSeleccionadoId: StateFlow<Long?> = _vehiculoSeleccionadoId

    val vehiculoSeleccionado: StateFlow<Vehiculo?> = combine(vehiculos, vehiculoSeleccionadoId) { lista, id ->
        lista.firstOrNull { it.id == id } ?: lista.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun seleccionarVehiculo(id: Long) { _vehiculoSeleccionadoId.value = id }

    fun agregarVehiculo(v: Vehiculo) = viewModelScope.launch {
        val id = repo.vehiculoDao.insertar(v)
        _vehiculoSeleccionadoId.value = id
    }

    fun editarVehiculo(v: Vehiculo) = viewModelScope.launch { repo.vehiculoDao.actualizar(v) }

    fun eliminarVehiculo(v: Vehiculo) = viewModelScope.launch { repo.vehiculoDao.eliminar(v) }

    // ---------- Helper genérico: datos ligados al vehículo seleccionado ----------
    private val idVehiculoFlow = vehiculoSeleccionado.map { it?.id }.distinctUntilChanged()

    private fun <T> flowPorVehiculo(obtener: (Long) -> Flow<List<T>>): StateFlow<List<T>> =
        idVehiculoFlow.flatMapLatest { id -> if (id == null) flowOf(emptyList()) else obtener(id) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cambiosAceite = flowPorVehiculo { repo.cambioAceiteDao.obtenerPorVehiculo(it) }
    val reparaciones = flowPorVehiculo { repo.reparacionDao.obtenerPorVehiculo(it) }
    val compraAutopartes = flowPorVehiculo { repo.compraAutoparteDao.obtenerPorVehiculo(it) }
    val incidentes = flowPorVehiculo { repo.incidenteDao.obtenerPorVehiculo(it) }
    val tecnomecanicas = flowPorVehiculo { repo.tecnomecanicaDao.obtenerPorVehiculo(it) }
    val soats = flowPorVehiculo { repo.soatDao.obtenerPorVehiculo(it) }
    val impuestos = flowPorVehiculo { repo.impuestoDao.obtenerPorVehiculo(it) }
    val otrosPagos = flowPorVehiculo { repo.otroPagoDao.obtenerPorVehiculo(it) }
    val enlaces = flowPorVehiculo { repo.enlaceDao.obtenerPorVehiculo(it) }
    val registrosOdometro = flowPorVehiculo { repo.registroOdometroDao.obtenerPorVehiculo(it) }
    val abastecimientos = flowPorVehiculo { repo.abastecimientoDao.obtenerPorVehiculo(it) }

    /** Último kilometraje registrado del vehículo seleccionado (null si no hay registros). */
    private val _ultimoKilometraje = MutableStateFlow<RegistroOdometro?>(null)
    val ultimoKilometraje: StateFlow<RegistroOdometro?> = _ultimoKilometraje

    // ---------- Altas ----------
    fun agregarCambioAceite(c: CambioAceite) = viewModelScope.launch { repo.cambioAceiteDao.insertar(c); actualizarResumenYAlertas() }
    fun agregarReparacion(r: Reparacion) = viewModelScope.launch { repo.reparacionDao.insertar(r); actualizarResumenYAlertas() }
    fun agregarCompraAutoparte(c: CompraAutoparte) = viewModelScope.launch { repo.compraAutoparteDao.insertar(c); actualizarResumenYAlertas() }
    fun agregarIncidente(i: Incidente) = viewModelScope.launch { repo.incidenteDao.insertar(i) }
    fun agregarTecnomecanica(t: Tecnomecanica) = viewModelScope.launch { repo.tecnomecanicaDao.insertar(t); actualizarResumenYAlertas() }
    fun agregarSoat(s: Soat) = viewModelScope.launch { repo.soatDao.insertar(s); actualizarResumenYAlertas() }
    fun agregarImpuesto(i: Impuesto) = viewModelScope.launch { repo.impuestoDao.insertar(i); actualizarResumenYAlertas() }
    fun agregarOtroPago(o: OtroPago) = viewModelScope.launch { repo.otroPagoDao.insertar(o); actualizarResumenYAlertas() }
    fun agregarEnlace(e: Enlace) = viewModelScope.launch { repo.enlaceDao.insertar(e) }
    fun agregarRegistroOdometro(r: RegistroOdometro) = viewModelScope.launch {
        repo.registroOdometroDao.insertar(r)
        actualizarUltimoKilometraje()
        actualizarResumenYAlertas()
    }
    fun agregarAbastecimiento(a: Abastecimiento) = viewModelScope.launch { repo.abastecimientoDao.insertar(a); actualizarResumenYAlertas() }

    // ---------- Ediciones ----------
    fun editarCambioAceite(c: CambioAceite) = viewModelScope.launch { repo.cambioAceiteDao.actualizar(c); actualizarResumenYAlertas() }
    fun editarReparacion(r: Reparacion) = viewModelScope.launch { repo.reparacionDao.actualizar(r); actualizarResumenYAlertas() }
    fun editarCompraAutoparte(c: CompraAutoparte) = viewModelScope.launch { repo.compraAutoparteDao.actualizar(c); actualizarResumenYAlertas() }
    fun editarIncidente(i: Incidente) = viewModelScope.launch { repo.incidenteDao.actualizar(i) }
    fun editarTecnomecanica(t: Tecnomecanica) = viewModelScope.launch { repo.tecnomecanicaDao.actualizar(t); actualizarResumenYAlertas() }
    fun editarSoat(s: Soat) = viewModelScope.launch { repo.soatDao.actualizar(s); actualizarResumenYAlertas() }
    fun editarImpuesto(i: Impuesto) = viewModelScope.launch { repo.impuestoDao.actualizar(i); actualizarResumenYAlertas() }
    fun editarOtroPago(o: OtroPago) = viewModelScope.launch { repo.otroPagoDao.actualizar(o); actualizarResumenYAlertas() }
    fun editarEnlace(e: Enlace) = viewModelScope.launch { repo.enlaceDao.actualizar(e) }
    fun editarAbastecimiento(a: Abastecimiento) = viewModelScope.launch { repo.abastecimientoDao.actualizar(a); actualizarResumenYAlertas() }

    // ---------- Eliminaciones ----------
    fun eliminarCambioAceite(c: CambioAceite) = viewModelScope.launch { repo.cambioAceiteDao.eliminar(c); actualizarResumenYAlertas() }
    fun eliminarReparacion(r: Reparacion) = viewModelScope.launch { repo.reparacionDao.eliminar(r); actualizarResumenYAlertas() }
    fun eliminarCompraAutoparte(c: CompraAutoparte) = viewModelScope.launch { repo.compraAutoparteDao.eliminar(c); actualizarResumenYAlertas() }
    fun eliminarIncidente(i: Incidente) = viewModelScope.launch { repo.incidenteDao.eliminar(i) }
    fun eliminarTecnomecanica(t: Tecnomecanica) = viewModelScope.launch { repo.tecnomecanicaDao.eliminar(t); actualizarResumenYAlertas() }
    fun eliminarSoat(s: Soat) = viewModelScope.launch { repo.soatDao.eliminar(s); actualizarResumenYAlertas() }
    fun eliminarImpuesto(i: Impuesto) = viewModelScope.launch { repo.impuestoDao.eliminar(i); actualizarResumenYAlertas() }
    fun eliminarOtroPago(o: OtroPago) = viewModelScope.launch { repo.otroPagoDao.eliminar(o); actualizarResumenYAlertas() }
    fun eliminarEnlace(e: Enlace) = viewModelScope.launch { repo.enlaceDao.eliminar(e) }
    fun eliminarAbastecimiento(a: Abastecimiento) = viewModelScope.launch { repo.abastecimientoDao.eliminar(a); actualizarResumenYAlertas() }
    fun eliminarRegistroOdometro(r: RegistroOdometro) = viewModelScope.launch {
        repo.registroOdometroDao.eliminar(r)
        actualizarUltimoKilometraje()
        actualizarResumenYAlertas()
    }

    // ---------- Resumen de gastos (total o filtrado por rango de fechas) ----------
    private val _resumenGastos = MutableStateFlow(Repository.ResumenGastos.VACIO)
    val resumenGastos: StateFlow<Repository.ResumenGastos> = _resumenGastos

    private val _rangoFechas = MutableStateFlow<RangoFechas?>(null)
    val rangoFechas: StateFlow<RangoFechas?> = _rangoFechas

    fun aplicarRangoFechas(desde: LocalDate, hasta: LocalDate) {
        _rangoFechas.value = RangoFechas(desde, hasta)
        viewModelScope.launch { actualizarResumenYAlertas() }
    }

    fun limpiarRangoFechas() {
        _rangoFechas.value = null
        viewModelScope.launch { actualizarResumenYAlertas() }
    }

    // ---------- Alertas del dashboard ----------
    private val _alertas = MutableStateFlow<List<Alerta>>(emptyList())
    val alertas: StateFlow<List<Alerta>> = _alertas

    init {
        viewModelScope.launch {
            idVehiculoFlow.collect { id ->
                if (id != null) {
                    actualizarResumenYAlertas()
                    actualizarUltimoKilometraje()
                } else {
                    _resumenGastos.value = Repository.ResumenGastos.VACIO
                    _alertas.value = emptyList()
                    _ultimoKilometraje.value = null
                }
            }
        }
    }

    /** Vuelve a calcular el resumen de gastos (según el rango activo) y las alertas del vehículo actual. */
    private suspend fun actualizarResumenYAlertas() {
        val id = vehiculoSeleccionado.value?.id ?: return
        val rango = _rangoFechas.value
        _resumenGastos.value = if (rango != null) {
            repo.resumenGastosEnRango(id, rango.desde, rango.hasta)
        } else {
            repo.resumenGastos(id)
        }
        calcularAlertas(id)
    }

    private suspend fun actualizarUltimoKilometraje() {
        val id = vehiculoSeleccionado.value?.id ?: return
        _ultimoKilometraje.value = repo.registroOdometroDao.obtenerUltimo(id)
    }

    private suspend fun calcularAlertas(vehiculoId: Long) {
        val hoy = LocalDate.now()
        val lista = mutableListOf<Alerta>()

        repo.soatDao.obtenerPorVehiculo(vehiculoId).first().forEach { soat ->
            val dias = ChronoUnit.DAYS.between(hoy, soat.fechaVencimiento)
            if (dias in 0..30) lista.add(Alerta("SOAT vence en $dias días", "${soat.fechaVencimiento}", dias <= 7))
        }
        repo.tecnomecanicaDao.obtenerPorVehiculo(vehiculoId).first().forEach { tm ->
            val dias = ChronoUnit.DAYS.between(hoy, tm.fechaVencimiento)
            if (dias in 0..30) lista.add(Alerta("Tecnomecánica vence en $dias días", "${tm.fechaVencimiento}", dias <= 7))
        }

        val ultimoKm = repo.registroOdometroDao.obtenerUltimo(vehiculoId)?.kilometraje
        val ultimoCambio = repo.cambioAceiteDao.obtenerUltimo(vehiculoId)
        if (ultimoKm != null && ultimoCambio != null) {
            val restante = ultimoCambio.proximoCambioKm - ultimoKm
            if (restante in 0..1000) lista.add(Alerta("Cambio de aceite cerca", "Faltan $restante km", restante <= 200))
        }

        _alertas.value = lista
    }

    // ---------- Backup ----------
    suspend fun exportarBackup(): File = backupManager.exportarAArchivo()

    fun importarBackup(uri: android.net.Uri) = viewModelScope.launch {
        backupManager.importarDesdeArchivo(uri)
    }

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    BitacoraViewModel(app) as T
            }
    }
}
