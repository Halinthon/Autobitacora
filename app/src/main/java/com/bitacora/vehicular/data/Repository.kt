package com.bitacora.vehicular.data

import android.content.Context

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

    /** Total gastado en un vehículo, sumando todas las categorías. */
    suspend fun totalGastado(vehiculoId: Long): Double {
        return cambioAceiteDao.sumaCostos(vehiculoId) +
            reparacionDao.sumaCostos(vehiculoId) +
            compraAutoparteDao.sumaCostos(vehiculoId) +
            tecnomecanicaDao.sumaCostos(vehiculoId) +
            soatDao.sumaCostos(vehiculoId) +
            impuestoDao.sumaCostos(vehiculoId) +
            otroPagoDao.sumaCostos(vehiculoId)
    }

    data class ResumenGastos(
        val aceite: Double,
        val reparaciones: Double,
        val autopartes: Double,
        val documentos: Double,
        val impuestos: Double,
        val otros: Double
    ) {
        val total: Double get() = aceite + reparaciones + autopartes + documentos + impuestos + otros
    }

    suspend fun resumenGastos(vehiculoId: Long): ResumenGastos = ResumenGastos(
        aceite = cambioAceiteDao.sumaCostos(vehiculoId),
        reparaciones = reparacionDao.sumaCostos(vehiculoId),
        autopartes = compraAutoparteDao.sumaCostos(vehiculoId),
        documentos = tecnomecanicaDao.sumaCostos(vehiculoId) + soatDao.sumaCostos(vehiculoId),
        impuestos = impuestoDao.sumaCostos(vehiculoId),
        otros = otroPagoDao.sumaCostos(vehiculoId)
    )
}
