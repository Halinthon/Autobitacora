package com.bitacora.vehicular.data

import android.content.Context
import java.time.LocalDate

class Repository(context: Context) {
    private val db = AppDatabase.obtener(context)

    val vehiculoDao = db.vehiculoDao()
    val cambioAceiteDao = db.cambioAceiteDao()
    val reparacionDao = db.reparacionDao()
    val compraAutoparteDao = db.compraAutoparteDao()
    val incidenteDao = db.incidenteDao()
    val tecnomecanicaDao = db.tecnomecanicaDao()
    val soatDao = db.soatDao()
    val impuestoDao = db.impuestoDao()
    val otroPagoDao = db.otroPagoDao()
    val enlaceDao = db.enlaceDao()
    val registroOdometroDao = db.registroOdometroDao()
    val abastecimientoDao = db.abastecimientoDao()
    val tarjetaPropiedadDao = db.tarjetaPropiedadDao()
    val licenciaConduccionDao = db.licenciaConduccionDao()

    data class ResumenGastos(
        val aceite: Double,
        val reparaciones: Double,
        val autopartes: Double,
        val documentos: Double,
        val impuestos: Double,
        val combustible: Double,
        val otros: Double
    ) {
        val total: Double get() = aceite + reparaciones + autopartes + documentos + impuestos + combustible + otros

        companion object {
            val VACIO = ResumenGastos(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }
    }

    /** Resumen de gastos total (sin filtro de fechas) de un vehículo. */
    suspend fun resumenGastos(vehiculoId: Long): ResumenGastos = ResumenGastos(
        aceite = cambioAceiteDao.sumaCostos(vehiculoId),
        reparaciones = reparacionDao.sumaCostos(vehiculoId),
        autopartes = compraAutoparteDao.sumaCostos(vehiculoId),
        documentos = tecnomecanicaDao.sumaCostos(vehiculoId) + soatDao.sumaCostos(vehiculoId),
        impuestos = impuestoDao.sumaCostos(vehiculoId),
        combustible = abastecimientoDao.sumaCostos(vehiculoId),
        otros = otroPagoDao.sumaCostos(vehiculoId)
    )

    /** Resumen de gastos filtrado por rango de fechas (inclusive) de un vehículo. */
    suspend fun resumenGastosEnRango(vehiculoId: Long, desde: LocalDate, hasta: LocalDate): ResumenGastos {
        val d = desde.toString()
        val h = hasta.toString()
        return ResumenGastos(
            aceite = cambioAceiteDao.sumaCostosEnRango(vehiculoId, d, h),
            reparaciones = reparacionDao.sumaCostosEnRango(vehiculoId, d, h),
            autopartes = compraAutoparteDao.sumaCostosEnRango(vehiculoId, d, h),
            documentos = tecnomecanicaDao.sumaCostosEnRango(vehiculoId, d, h) + soatDao.sumaCostosEnRango(vehiculoId, d, h),
            impuestos = impuestoDao.sumaCostosEnRango(vehiculoId, d, h),
            combustible = abastecimientoDao.sumaCostosEnRango(vehiculoId, d, h),
            otros = otroPagoDao.sumaCostosEnRango(vehiculoId, d, h)
        )
    }
}
